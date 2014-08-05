/*
 * jEdit.java - Main class of the jEdit editor
 * Copyright (C) 1998, 1999, 2000, 2001 Slava Pestov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.gjt.sp.jedit;

import com.microstar.xml.*;
import javax.swing.text.Element;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.MessageFormat;
import java.util.*;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.search.SearchAndReplace;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.util.Log;

/**
 * The main class of the jEdit text editor.
 * @author Slava Pestov
 * @version $Id$
 */
public class jEdit
{
	/**
	 * Returns the jEdit version as a human-readable string.
	 */
	public static String getVersion()
	{
		return MiscUtilities.buildToVersion(getBuild());
	}

	/**
	 * Returns the internal version. String.compareTo() can be used
	 * to compare different internal versions.
	 */
	public static String getBuild()
	{
		// (major).(minor).(<99 = preX, 99 = final).(bug fix)
		return "03.02.01.00";
	}

	/**
	 * The main method of the jEdit application.
	 * <p>
	 * This should never be invoked directly.
	 * @param args The command line arguments
	 */
	public static void main(String[] args)
	{
		// for developers: run 'jedit 0' to get extensive logging
		int level = Log.WARNING;
		if(args.length >= 1)
		{
			String levelStr = args[0];
			if(levelStr.length() == 1 && Character.isDigit(
				levelStr.charAt(0)))
			{
				level = Integer.parseInt(levelStr);
				args[0] = null;
			}
		}

		// Parse command line
		boolean endOpts = false;
		boolean newView = false;
		settingsDirectory = MiscUtilities.constructPath(
			System.getProperty("user.home"),".jedit");
		String portFile = "server";
		boolean restore = true;
		boolean showSplash = true;
		boolean showGUI = true;

		for(int i = 0; i < args.length; i++)
		{
			String arg = args[i];
			if(arg == null)
				continue;
			else if(arg.length() == 0)
				args[i] = null;
			else if(arg.startsWith("-") && !endOpts)
			{
				if(arg.equals("--"))
					endOpts = true;
				else if(arg.equals("-usage"))
				{
					version();
					System.err.println();
					usage();
					System.exit(1);
				}
				else if(arg.equals("-version"))
				{
					version();
					System.exit(1);
				}
				else if(arg.equals("-nosettings"))
					settingsDirectory = null;
				else if(arg.startsWith("-settings="))
					settingsDirectory = arg.substring(10);
				else if(arg.startsWith("-noserver"))
					portFile = null;
				else if(arg.startsWith("-server="))
					portFile = arg.substring(8);
				else if(arg.startsWith("-background"))
					background = true;
				else if(arg.startsWith("-nogui"))
					showGUI = false;
				else if(arg.equals("-norestore"))
					restore = false;
				else if(arg.equals("-nosplash"))
					showSplash = false;
				else if(arg.equals("-newview"))
					newView = true;
				else
				{
					System.err.println("Unknown option: "
						+ arg);
					usage();
					System.exit(1);
				}
				args[i] = null;
			}
		}

		if(settingsDirectory != null && portFile != null)
			portFile = MiscUtilities.constructPath(settingsDirectory,portFile);
		else
			portFile = null;

		// Try connecting to another running jEdit instance
		String userDir = System.getProperty("user.dir");

		if(portFile != null && new File(portFile).exists())
		{
			int port, key;
			try
			{
				BufferedReader in = new BufferedReader(new FileReader(portFile));
				port = Integer.parseInt(in.readLine());
				key = Integer.parseInt(in.readLine());
				in.close();

				Socket socket = new Socket(InetAddress.getLocalHost(),port);
				Writer out = new OutputStreamWriter(socket.getOutputStream());
				out.write(String.valueOf(key));
				out.write('\n');

				if(!restore)
					out.write("norestore\n");

				if(newView)
					out.write("newview\n");
				out.write("parent=" + userDir + "\n");
				out.write("--\n");

				for(int i = 0; i < args.length; i++)
				{
					if(args[i] != null)
					{
						out.write(args[i]);
						out.write('\n');
					}
				}

				out.close();

				System.exit(0);
			}
			catch(Exception e)
			{
				// ok, this one seems to confuse newbies
				// endlessly, so log it as NOTICE, not
				// ERROR
				Log.log(Log.NOTICE,jEdit.class,"An error occurred"
					+ " while connecting to the jEdit server instance.");
				Log.log(Log.NOTICE,jEdit.class,"This probably means that"
					+ " jEdit crashed and/or exited abnormally");
				Log.log(Log.NOTICE,jEdit.class,"the last time it was run.");
				Log.log(Log.NOTICE,jEdit.class,"If you don't"
					+ " know what this means, don't worry.");
				Log.log(Log.NOTICE,jEdit.class,e);
			}
		}

		// Show the kool splash screen
		if(showSplash)
			GUIUtilities.showSplashScreen();

		// Initialize activity log and settings directory
		Writer stream;
		if(settingsDirectory != null)
		{
			File _settingsDirectory = new File(settingsDirectory);
			if(!_settingsDirectory.exists())
				_settingsDirectory.mkdirs();
			File _macrosDirectory = new File(settingsDirectory,"macros");
			if(!_macrosDirectory.exists())
				_macrosDirectory.mkdir();

			String logPath = MiscUtilities.constructPath(
				settingsDirectory,"activity.log");

			try
			{
				stream = new BufferedWriter(new FileWriter(logPath));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				stream = null;
			}
		}
		else
		{
			stream = null;
		}

		Log.init(true,level,stream);
		Log.log(Log.NOTICE,jEdit.class,"jEdit version " + getVersion());
		Log.log(Log.MESSAGE,jEdit.class,"Settings directory is "
			+ settingsDirectory);

		// Initialize server
		if(portFile != null)
		{
			server = new EditServer(portFile);
			if(!server.isOK())
				server = null;
		}
		else
		{
			if(background)
			{
				background = false;
				System.err.println("You cannot specify both the"
					+ " -background and -noserver switches");
			}
		}

		// Get things rolling
		initMisc();
		initSystemProperties();
		BeanShell.init();
		GUIUtilities.advanceSplashProgress();
		initSiteProperties();
		initUserProperties();
		initActions();
		initPlugins();

		if(settingsDirectory != null)
		{
			File history = new File(MiscUtilities.constructPath(
				settingsDirectory,"history"));
			if(history.exists())
				historyModTime = history.lastModified();
			HistoryModel.loadHistory(history);

			File recent = new File(MiscUtilities.constructPath(
				settingsDirectory,"recent"));
			if(recent.exists())
				recentModTime = recent.lastModified();
			BufferHistory.load(recent);
		}

		Abbrevs.load();

		GUIUtilities.advanceSplashProgress();

		// Buffer sort
		sortBuffers = getBooleanProperty("sortBuffers");
		sortByName = getBooleanProperty("sortByName");

		initPLAF();
		initModes();

		GUIUtilities.advanceSplashProgress();

		SearchAndReplace.load();
		FavoritesVFS.loadFavorites();
		Macros.loadMacros();
		propertiesChanged();

		GUIUtilities.advanceSplashProgress();

		// Start plugins
		for(int i = 0; i < jars.size(); i++)
		{
			((EditPlugin.JAR)jars.elementAt(i)).getClassLoader()
				.loadAllPlugins();
		}

		GUIUtilities.advanceSplashProgress();

		Buffer buffer = openFiles(userDir,args);

		if(restore && bufferCount == 0
			&& !background
			&& settingsDirectory != null
			&& jEdit.getBooleanProperty("restore"))
			buffer = restoreOpenFiles();

		// Create the view and hide the splash screen.
		final boolean _showGUI = showGUI;
		final Buffer _buffer = buffer;

		GUIUtilities.advanceSplashProgress();

		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				EditBus.send(new EditorStarted(null));

				// If no files to open were specified in
				// background mode, don't create a view.
				if(background && !_showGUI)
				{
					if(bufferCount != 0)
						newView(null,_buffer);
				}
				else
				{
					if(bufferCount == 0)
						newFile(null);
					newView(null,_buffer);
				}

				// execute startup macro
				Macros.Macro macro = Macros.getMacro("Startup");
				if(macro != null)
				{
					Log.log(Log.NOTICE,jEdit.class,"Running startup macro");
					BeanShell.runScript(viewsFirst,macro.path,false,false);
				}

				// if there is a view around, show tip of the day
				if(viewCount != 0)
				{
					if(jEdit.getBooleanProperty("firstTime"))
						new HelpViewer("welcome.html");
					else if(jEdit.getBooleanProperty("tip.show"))
						new TipOfTheDay(viewsFirst);

					setBooleanProperty("firstTime",false);
				}

				GUIUtilities.hideSplashScreen();
				Log.log(Log.MESSAGE,jEdit.class,"Startup "
					+ "complete");

				// Start I/O threads
				VFSManager.start();

				// Start edit server
				if(server != null)
					server.start();
			}
		});
	}

	/**
	 * Loads the properties from the specified input stream. This
	 * calls the <code>load()</code> method of the properties object
	 * and closes the stream.
	 * @param in The input stream
	 * @param def If true, the properties will be loaded into the
	 * default table
	 * @exception IOException if an I/O error occured
	 */
	public static void loadProps(InputStream in, boolean def)
		throws IOException
	{
		in = new BufferedInputStream(in);
		if(def)
			defaultProps.load(in);
		else
			props.load(in);
		in.close();
	}

	/**
	 * Returns the properties object which contains all known
	 * jEdit properties.
	 * @since jEdit 3.1pre4
	 */
	public static final Properties getProperties()
	{
		return props;
	}

	/**
	 * Fetches a property, returning null if it's not defined.
	 * @param name The property
	 */
	public static final String getProperty(String name)
	{
		return props.getProperty(name);
	}

	/**
	 * Fetches a property, returning the default value if it's not
	 * defined.
	 * @param name The property
	 * @param def The default value
	 */
	public static final String getProperty(String name, String def)
	{
		return props.getProperty(name,def);
	}

	/**
	 * Returns the property with the specified name, formatting it with
	 * the <code>java.text.MessageFormat.format()</code> method.
	 * @param name The property
	 * @param args The positional parameters
	 */
	public static final String getProperty(String name, Object[] args)
	{
		if(name == null)
			return null;
		if(args == null)
			return props.getProperty(name,name);
		else
			return MessageFormat.format(props.getProperty(name,
				name),args);
	}

	/**
	 * Returns the value of a boolean property.
	 * @param name The property
	 */
	public static final boolean getBooleanProperty(String name)
	{
		return getBooleanProperty(name,false);
	}

	/**
	 * Returns the value of a boolean property.
	 * @param name The property
	 * @param def The default value
	 */
	public static final boolean getBooleanProperty(String name, boolean def)
	{
		String value = getProperty(name);
		if(value == null)
			return def;
		else if(value.equals("true") || value.equals("yes")
			|| value.equals("on"))
			return true;
		else if(value.equals("false") || value.equals("no")
			|| value.equals("off"))
			return false;
		else
			return def;
	}

	/**
	 * Sets a property to a new value.
	 * @param name The property
	 * @param value The new value
	 */
	public static final void setProperty(String name, String value)
	{
		/* if value is null:
		 * - if default is null, unset user prop
		 * - else set user prop to ""
		 * else
		 * - if default equals value, ignore
		 * - if default doesn't equal value, set user
		 */
		if(value == null || value.length() == 0)
		{
			String prop = (String)defaultProps.get(name);
			if(prop == null || prop.length() == 0)
				props.remove(name);
			else
				props.put(name,"");
		}
		else
		{
			String prop = (String)defaultProps.get(name);
			if(value.equals(prop))
				props.remove(name);
			else
				props.put(name,value);
		}
	}

	/**
	 * Sets a property to a new value. Properties set using this
	 * method are not saved to the user properties list.
	 * @param name The property
	 * @param value The new value
	 * @since jEdit 2.3final
	 */
	public static final void setTemporaryProperty(String name, String value)
	{
		props.remove(name);
		defaultProps.put(name,value);
	}

	/**
	 * @deprecated As of jEdit 2.3final. Use setTemporaryProperty()
	 * instead.
	 */
	public static final void setDefaultProperty(String name, String value)
	{
		setTemporaryProperty(name,value);
	}

	/**
	 * Sets a boolean property.
	 * @param name The property
	 * @param value The value
	 */
	public static final void setBooleanProperty(String name, boolean value)
	{
		setProperty(name,value ? "true" : "false");
	}

	/**
	 * Unsets (clears) a property.
	 * @param name The property
	 */
	public static final void unsetProperty(String name)
	{
		if(defaultProps.get(name) != null)
			props.put(name,"");
		else
			props.remove(name);
	}

	/**
	 * Resets a property to its default value.
	 * @param name The property
	 *
	 * @since jEdit 2.5pre3
	 */
	public static final void resetProperty(String name)
	{
		props.remove(name);
	}

	/**
	 * Reloads various settings from the properties.
	 */
	public static void propertiesChanged()
	{
		initKeyBindings();

		int interval;
		try
		{
			interval = Integer.parseInt(getProperty("autosave"));
		}
		catch(NumberFormatException nf)
		{
			Log.log(Log.ERROR,jEdit.class,nf);
			interval = 30;
		}
		Autosave.setInterval(interval);

		saveCaret = getBooleanProperty("saveCaret");

		EditBus.send(new PropertiesChanged(null));
	}

	/**
	 * Returns a list of plugin JARs that are not currently loaded
	 * by examining the user and system plugin directories.
	 * @since jEdit 3.2pre1
	 */
	public static String[] getNotLoadedPluginJARs()
	{
		Vector returnValue = new Vector();

		String systemPluginDir = MiscUtilities
			.constructPath(jEditHome,"jars");

		String[] list = new File(systemPluginDir).list();
		if(list != null)
			getNotLoadedPluginJARs(returnValue,systemPluginDir,list);

		if(settingsDirectory != null)
		{
			String userPluginDir = MiscUtilities
				.constructPath(settingsDirectory,"jars");
			list = new File(userPluginDir).list();
			if(list != null)
			{
				getNotLoadedPluginJARs(returnValue,
					userPluginDir,list);
			}
		}

		String[] _returnValue = new String[returnValue.size()];
		returnValue.copyInto(_returnValue);
		return _returnValue;
	}

	/**
	 * Loads all plugins in a directory.
	 * @param directory The directory
	 */
	public static void loadPlugins(String directory)
	{
		Log.log(Log.NOTICE,jEdit.class,"Loading plugins from "
			+ directory);

		File file = new File(directory);
		if(!(file.exists() && file.isDirectory()))
			return;
		String[] plugins = file.list();
		if(plugins == null)
			return;

		MiscUtilities.quicksort(plugins,new MiscUtilities.StringICaseCompare());
		for(int i = 0; i < plugins.length; i++)
		{
			String plugin = plugins[i];
			if(!plugin.toLowerCase().endsWith(".jar"))
				continue;

			String path = MiscUtilities.constructPath(directory,plugin);

			if(plugin.equals("BeanShell.jar")
				|| plugin.equals("bsh-1.0.jar")
				|| plugin.equals("EditBuddy.jar")
				|| plugin.equals("PluginManager.jar"))
			{
				String[] args = { plugin };
				GUIUtilities.error(null,"plugin.obsolete",args);
				continue;
			}

			try
			{
				Log.log(Log.DEBUG,jEdit.class,
					"Scanning JAR file: " + path);
				new JARClassLoader(path);
			}
			catch(IOException io)
			{
				Log.log(Log.ERROR,jEdit.class,"Cannot load"
					+ " plugin " + plugin);
				Log.log(Log.ERROR,jEdit.class,io);

				String[] args = { plugin, io.toString() };
				GUIUtilities.error(null,"plugin.load-error",args);
			}
		}
	}

	/**
	 * Adds a plugin to the editor.
	 * @param plugin The plugin
	 */
	public static void addPlugin(EditPlugin plugin)
	{
		plugins.addPlugin(plugin);
	}

	/**
	 * Adds a plugin to the editor.
	 * @param plugin The plugin
	 */
	public static void addPluginJAR(EditPlugin.JAR plugin)
	{
		plugin.index = jars.size();
		jars.addElement(plugin);
	}

	/**
	 * Returns the plugin with the specified class name.
	 */
	public static EditPlugin getPlugin(String name)
	{
		EditPlugin[] plugins = getPlugins();
		for(int i = 0; i < plugins.length; i++)
		{
			if(plugins[i].getClassName().equals(name))
				return plugins[i];
		}

		return null;
	}

	/**
	 * Returns an array of installed plugins.
	 */
	public static EditPlugin[] getPlugins()
	{
		Vector vector = new Vector();
		for(int i = 0; i < jars.size(); i++)
		{
			((EditPlugin.JAR)jars.elementAt(i)).getPlugins(vector);
		}
		plugins.getPlugins(vector);

		EditPlugin[] array = new EditPlugin[vector.size()];
		vector.copyInto(array);
		return array;
	}

	/**
	 * Returns an array of installed plugins.
	 * @since jEdit 2.5pre3
	 */
	public static EditPlugin.JAR[] getPluginJARs()
	{
		EditPlugin.JAR[] array = new EditPlugin.JAR[jars.size()];
		jars.copyInto(array);
		return array;
	}

	/**
	 * Returns the JAR with the specified path name.
	 * @param path The path name
	 * @since jEdit 2.6pre1
	 */
	public static EditPlugin.JAR getPluginJAR(String path)
	{
		for(int i = 0; i < jars.size(); i++)
		{
			EditPlugin.JAR jar = (EditPlugin.JAR)jars.elementAt(i);
			if(jar.getPath().equals(path))
				return jar;
		}

		return null;
	}

	/**
	 * Returns the JAR at the specified index.
	 * @since jEdit 2.5pre3
	 */
	public static EditPlugin.JAR getPluginJAR(int index)
	{
		return (EditPlugin.JAR)jars.elementAt(index);
	}

	/**
	 * Loads the specified action list.
	 * @since jEdit 3.1pre1
	 */
	public static boolean loadActions(String path, Reader in, boolean plugin)
	{
		Log.log(Log.DEBUG,jEdit.class,"Loading actions from " + path);

		ActionListHandler ah = new ActionListHandler(path,plugin);
		XmlParser parser = new XmlParser();
		parser.setHandler(ah);
		try
		{
			parser.parse(null, null, in);
			return true;
		}
		catch(XmlException xe)
		{
			int line = xe.getLine();
			String message = xe.getMessage();
			Log.log(Log.ERROR,jEdit.class,path + ":" + line
				+ ": " + message);
		}
		catch(Exception e)
		{
			Log.log(Log.ERROR,jEdit.class,e);
		}

		return false;
	}

	/**
	 * Plugins should not be calling this method.
	 */
	public static void addAction(EditAction action)
	{
		actionHash.put(action.getName(),action);
	}

	/**
	 * Returns a named action.
	 * @param action The action
	 */
	public static EditAction getAction(String action)
	{
		return (EditAction)actionHash.get(action);
	}

	/**
	 * Returns the list of actions registered with the editor.
	 */
	public static EditAction[] getActions()
	{
		EditAction[] actions = new EditAction[actionHash.size()];
		Enumeration enum = actionHash.elements();
		int i = 0;
		while(enum.hasMoreElements())
		{
			actions[i++] = (EditAction)enum.nextElement();
		}
		return actions;
	}

	/**
	 * Registers an edit mode with the editor.
	 * @param mode The edit mode
	 */
	public static void addMode(Mode mode)
	{
		Log.log(Log.DEBUG,jEdit.class,"Adding edit mode "
			+ mode.getName());

		mode.init();
		modes.addElement(mode);
	}

	/**
	 * Loads a mode catalog file.
	 * @param directory The directory containing the catalog file
	 */
	public static void loadModeCatalog(String path)
	{
		Log.log(Log.MESSAGE,jEdit.class,"Loading mode catalog file " + path);

		ModeCatalogHandler handler = new ModeCatalogHandler(
			MiscUtilities.getParentOfPath(path));
		XmlParser parser = new XmlParser();
		parser.setHandler(handler);
		try
		{
			BufferedReader in = new BufferedReader(
				new InputStreamReader(
				new FileInputStream(path)));
			parser.parse(null, null, in);
		}
		catch(XmlException xe)
		{
			int line = xe.getLine();
			String message = xe.getMessage();
			Log.log(Log.ERROR,jEdit.class,path + ":" + line
				+ ": " + message);
		}
		catch(Exception e)
		{
			Log.log(Log.ERROR,jEdit.class,e);
		}
	}

	/**
	 * Reloads all edit modes.
	 * @param view The view
	 */
	/* public static void reloadModes(View view)
	{
		view.showWaitCursor();

		String path;
		String settingsDirectory = jEdit.getSettingsDirectory();

		if(settingsDirectory == null)
			path = null;
		else
			path = MiscUtilities.constructPath(settingsDirectory,
				"mode-cache");

		jEdit.createModeCache(path);

		Buffer[] buffers = jEdit.getBuffers();
		for(int i = 0; i < buffers.length; i++)
			buffers[i].setMode();

		View[] views = jEdit.getViews();
		for(int i = 0; i < views.length; i++)
		{
			EditPane[] editPanes = views[i].getEditPanes();
			for(int j = 0; j < editPanes.length; j++)
				editPanes[j].getTextArea().repaint();
		}

		view.hideWaitCursor();
	} */

	/**
	 * Loads an XML-defined edit mode from the specified reader.
	 * @param mode The edit mode
	 */
	public static void loadMode(Mode mode)
	{
		String fileName = (String)mode.getProperty("file");

		Log.log(Log.NOTICE,jEdit.class,"Loading edit mode " + fileName);

		XmlParser parser = new XmlParser();
		XModeHandler xmh = new XModeHandler(parser,mode.getName(),fileName);
		parser.setHandler(xmh);
		try
		{
			Reader grammar = new BufferedReader(new FileReader(fileName));
			parser.parse(null, null, grammar);
		}
		catch (Exception e)
		{
			Log.log(Log.ERROR, jEdit.class, e);

			if (e instanceof XmlException)
			{
				XmlException xe = (XmlException) e;
				int line = xe.getLine();
				String message = xe.getMessage();

				Object[] args = { fileName, new Integer(line), message };
				GUIUtilities.error(null,"xmode-parse",args);
			}

			// give it an empty token marker to avoid problems
			TokenMarker marker = new TokenMarker();
			marker.addRuleSet("MAIN",new ParserRuleSet());
			mode.setTokenMarker(marker);
		}
	}

	/**
	 * Returns the edit mode with the specified name.
	 * @param name The edit mode
	 */
	public static Mode getMode(String name)
	{
		for(int i = 0; i < modes.size(); i++)
		{
			Mode mode = (Mode)modes.elementAt(i);
			if(mode.getName().equals(name))
				return mode;
		}
		return null;
	}

	/**
	 * Returns the localised name of an edit mode.
	 * @param mode The edit mode
	 */
	public static String getModeName(Mode mode)
	{
		return jEdit.props.getProperty("mode." +
			mode.getName() + ".name");
	}

	/**
	 * Returns an array of installed edit modes.
	 */
	public static Mode[] getModes()
	{
		Mode[] array = new Mode[modes.size()];
		modes.copyInto(array);
		return array;
	}

	/**
	 * Displays the open file dialog box, and opens any selected files.
	 * @param view The view
	 * @since jEdit 2.7pre2
	 */
	public static void showOpenFileDialog(View view)
	{
		String[] files = GUIUtilities.showVFSFileDialog(view,null,
			VFSBrowser.OPEN_DIALOG,true);

		Buffer buffer = null;
		if(files != null)
		{
			for(int i = 0; i < files.length; i++)
			{
				Buffer newBuffer = openFile(null,files[i]);
				if(newBuffer != null)
					buffer = newBuffer;
			}
		}

		if(buffer != null)
			view.setBuffer(buffer);
	}

	/**
	 * Opens files that were open last time.
	 * @since jEdit 3.1pre4
	 */
	public static Buffer restoreOpenFiles()
	{
		if(settingsDirectory == null)
			return null;

		File session = new File(MiscUtilities.constructPath(
			settingsDirectory,"session"));

		if(!session.exists())
			return null;

		Buffer buffer = null;
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(
				session));

			String line;
			while((line = in.readLine()) != null)
			{
				boolean current;
				if(line.endsWith("\t*"))
				{
					line = line.substring(0,line.length() - 2);
					current = true;
				}
				else
					current = false;

				Buffer _buffer = openFile(null,line);
				if(current && _buffer != null)
					buffer = _buffer;
			}

			in.close();
		}
		catch(IOException io)
		{
			Log.log(Log.ERROR,jEdit.class,"Error while loading " + session);
			Log.log(Log.ERROR,jEdit.class,io);
		}

		return buffer;
	}

	/**
	 * Saves the list of open files.
	 * @since jEdit 3.1pre5
	 */
	public static void saveOpenFiles(View view)
	{
		if(settingsDirectory == null)
			return;

		view.getEditPane().saveCaretInfo();
		Buffer current = view.getBuffer();

		File session = new File(MiscUtilities.constructPath(
			settingsDirectory,"session"));

		try
		{
			String lineSep = System.getProperty("line.separator");

			BufferedWriter out = new BufferedWriter(new FileWriter(
				session));
			Buffer buffer = buffersFirst;
			while(buffer != null)
			{
				out.write(buffer.getPath());
				if(buffer == current)
					out.write("\t*");
				out.write(lineSep);
				buffer = buffer.next;
			}
			out.close();
		}
		catch(IOException io)
		{
			Log.log(Log.ERROR,jEdit.class,"Error while saving " + session);
			Log.log(Log.ERROR,jEdit.class,io);
		}
	}

	/**
	 * Opens the file names specified in the argument array. This
	 * handles +line and +marker arguments just like the command
	 * line parser.
	 * @param parent The parent directory
	 * @param args The file names to open
	 * @since jEdit 2.6pre4
	 */
	public static Buffer openFiles(String parent, String[] args)
	{
		Buffer retVal = null;
		Buffer lastBuffer = null;

		for(int i = 0; i < args.length; i++)
		{
			String arg = args[i];
			if(arg == null)
				continue;
			else if(arg.startsWith("+line:") || arg.startsWith("+marker:"))
			{
				if(lastBuffer != null)
					gotoMarker(lastBuffer,arg);
				continue;
			}

			lastBuffer = openFile(null,parent,arg,false,false);

			if(retVal == null && lastBuffer != null)
				retVal = lastBuffer;
		}

		return retVal;
	}

	/**
	 * Opens a file. Note that as of jEdit 2.5pre1, this may return
	 * null if the buffer could not be opened.
	 * @param view The view to open the file in
	 * @param path The file path
	 *
	 * @since jEdit 2.4pre1
	 */
	public static Buffer openFile(View view, String path)
	{
		return openFile(view,null,path,false,false,
			new Hashtable());
	}

	/**
	 * Opens a file. Note that as of jEdit 2.5pre1, this may return
	 * null if the buffer could not be opened.
	 * @param view The view to open the file in
	 * @param parent The parent directory of the file
	 * @param path The path name of the file
	 * @param readOnly True if the file should be read only
	 * @param newFile True if the file should not be loaded from disk
	 * be prompted if it should be reloaded
	 */
	public static Buffer openFile(View view, String parent,
		String path, boolean readOnly, boolean newFile)
	{
		return openFile(view,parent,path,readOnly,newFile,
			new Hashtable());
	}

	/**
	 * Opens a file. Note that as of jEdit 2.5pre1, this may return
	 * null if the buffer could not be opened.
	 * @param view The view to open the file in
	 * @param parent The parent directory of the file
	 * @param path The path name of the file
	 * @param readOnly True if the file should be read only
	 * @param newFile True if the file should not be loaded from disk
	 * be prompted if it should be reloaded
	 * @param props Buffer-local properties to set in the buffer
	 *
	 * @since JEdit 2.5pre1
	 */
	public static Buffer openFile(final View view, String parent,
		String path, boolean readOnly, boolean newFile,
		Hashtable props)
	{
		if(view != null && parent == null)
		{
			File file = view.getBuffer().getFile();
			if(file != null)
				parent = file.getParent();
		}

		String protocol;
		if(MiscUtilities.isURL(path))
		{
			protocol = MiscUtilities.getProtocolOfURL(path);
			if(protocol.equals("file"))
				path = path.substring(5);
		}
		else
			protocol = "file";

		if(protocol.equals("file"))
			path = MiscUtilities.constructPath(parent,path);

		Buffer buffer = getBuffer(path);
		if(buffer != null)
		{
			if(view != null)
				view.setBuffer(buffer);
			return buffer;
		}

		if(saveCaret && props.get(Buffer.CARET) == null)
		{
			int caret = BufferHistory.getCaretPosition(path);
			props.put(Buffer.CARET,new Integer(caret));
		}

		final Buffer newBuffer = new Buffer(view,path,readOnly,
			newFile,false,props);

		if(!newBuffer.load(view,false))
			return null;

		addBufferToList(newBuffer);

		EditBus.send(new BufferUpdate(newBuffer,view,BufferUpdate.CREATED));

		if(view != null)
			view.setBuffer(newBuffer);

		return newBuffer;
	}

	/**
	 * Opens a temporary buffer. A temporary buffer is like a normal
	 * buffer, except that an event is not fired, the the buffer is
	 * not added to the buffers list.
	 *
	 * @param view The view to open the file in
	 * @param parent The parent directory of the file
	 * @param path The path name of the file
	 * @param readOnly True if the file should be read only
	 * @param newFile True if the file should not be loaded from disk
	 */
	public static Buffer openTemporary(View view, String parent,
		String path, boolean readOnly, boolean newFile)
	{
		if(view != null && parent == null)
		{
			File file = view.getBuffer().getFile();
			if(file != null)
				parent = file.getParent();
		}

		String protocol;
		if(MiscUtilities.isURL(path))
		{
			protocol = MiscUtilities.getProtocolOfURL(path);
			if(protocol.equals("file"))
				path = path.substring(5);
		}
		else
			protocol = "file";
			
		if(protocol.equals("file"))
			path = MiscUtilities.constructPath(parent,path);

		Buffer buffer = getBuffer(path);
		if(buffer != null)
			return buffer;

		buffer = new Buffer(null,path,readOnly,newFile,true,
			new Hashtable());
		if(!buffer.load(view,false))
			return null;
		else
			return buffer;
	}

	/**
	 * Adds a temporary buffer to the buffer list. This must be done
	 * before allowing the user to interact with the buffer in any
	 * way.
	 * @param buffer The buffer
	 */
	public static void commitTemporary(Buffer buffer)
	{
		if(!buffer.isTemporary())
			return;

		buffer.setMode();
		buffer.propertiesChanged();

		addBufferToList(buffer);
		buffer.commitTemporary();

		EditBus.send(new BufferUpdate(buffer,null,BufferUpdate.CREATED));
	}

	/**
	 * Creates a new `untitled' file.
	 * @param view The view to create the file in
	 */
	public static Buffer newFile(View view)
	{
		return newFile(view,null);
	}

	/**
	 * Creates a new `untitled' file.
	 * @param view The view to create the file in
	 * @param dir The directory to create the file in
	 * @since jEdit 3.1pre2
	 */
	public static Buffer newFile(View view, String dir)
	{
		// If only one new file is open which is clean, just close
		// it, which will create an 'Untitled-1'
		if(dir != null
			&& buffersFirst != null
			&& buffersFirst == buffersLast
			&& buffersFirst.isUntitled()
			&& !buffersFirst.isDirty())
		{
			closeBuffer(view,buffersFirst);
			// return the newly created 'untitled-1'
			return buffersFirst;
		}

		// Find the highest Untitled-n file
		int untitledCount = 0;
		Buffer buffer = buffersFirst;
		while(buffer != null)
		{
			if(buffer.getName().startsWith("Untitled-"))
			{
				try
				{
					untitledCount = Math.max(untitledCount,
						Integer.parseInt(buffer.getName()
						.substring(9)));
				}
				catch(NumberFormatException nf)
				{
				}
			}
			buffer = buffer.next;
		}

		return openFile(view,dir,"Untitled-" + (untitledCount+1),
			false,true);
	}

	/**
	 * Closes a buffer. If there are unsaved changes, the user is
	 * prompted if they should be saved first.
	 * @param view The view
	 * @param buffer The buffer
	 * @return True if the buffer was really closed, false otherwise
	 */
	public static boolean closeBuffer(View view, Buffer buffer)
	{
		// Wait for pending I/O requests
		if(buffer.isPerformingIO())
		{
			VFSManager.waitForRequests();
			if(VFSManager.errorOccurred())
				return false;
		}

		if(buffer.isDirty())
		{
			Object[] args = { buffer.getName() };
			int result = GUIUtilities.confirm(view,"notsaved",args,
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE);
			if(result == JOptionPane.YES_OPTION)
			{
				if(!buffer.save(view,null,true))
					return false;
			}
			else if(result != JOptionPane.NO_OPTION)
				return false;
		}

		_closeBuffer(view,buffer);

		return true;
	}

	/**
	 * Closes the buffer, even if it has unsaved changes.
	 * @param view The view
	 * @param buffer The buffer
	 *
	 * @since jEdit 2.2pre1
	 */
	public static void _closeBuffer(View view, Buffer buffer)
	{
		if(buffer.isClosed())
		{
			// can happen if the user presses C+w twice real
			// quick and the buffer has unsaved changes
			return;
		}

		if(!buffer.isNewFile())
		{
			view.getEditPane().saveCaretInfo();
			Integer _caret = (Integer)buffer.getProperty(Buffer.CARET);
			int caret = (_caret == null ? 0 : _caret.intValue());
			BufferHistory.setCaretPosition(buffer.getPath(),caret);
		}

		removeBufferFromList(buffer);
		buffer.close();

		EditBus.send(new BufferUpdate(buffer,view,BufferUpdate.CLOSED));

		// Create a new file when the last is closed
		if(buffersFirst == null && buffersLast == null)
			newFile(view);
	}

	/**
	 * Closes all open buffers.
	 * @param view The view
	 */
	public static boolean closeAllBuffers(View view)
	{
		return closeAllBuffers(view,false);
	}

	/**
	 * Closes all open buffers.
	 * @param view The view
	 * @param isExiting This must be false unless this method is
	 * being called by the exit() method
	 */
	public static boolean closeAllBuffers(View view, boolean isExiting)
	{
		boolean dirty = false;

		Buffer buffer = buffersFirst;
		while(buffer != null)
		{
			if(buffer.isDirty())
			{
				dirty = true;
				break;
			}
			buffer = buffer.next;
		}

		if(dirty)
		{
			boolean ok = new CloseDialog(view).isOK();
			if(!ok)
				return false;
		}

		// Wait for pending I/O requests
		VFSManager.waitForRequests();
		if(VFSManager.errorOccurred())
			return false;

		// close remaining buffers (the close dialog only deals with
		// dirty ones)

		buffer = buffersFirst;

		// zero it here so that BufferTabs doesn't have any problems
		buffersFirst = buffersLast = null;
		bufferCount = 0;

		while(buffer != null)
		{
			if(!buffer.isNewFile())
			{
				Integer _caret = (Integer)buffer.getProperty(Buffer.CARET);
				int caret = (_caret == null ? 0 : _caret.intValue());
				BufferHistory.setCaretPosition(buffer.getPath(),caret);
			}

			buffer.close();
			if(!isExiting)
			{
				EditBus.send(new BufferUpdate(buffer,view,
					BufferUpdate.CLOSED));
			}
			buffer = buffer.next;
		}

		if(!isExiting)
			newFile(view);

		return true;
	}

	/**
	 * Saves all open buffers.
	 * @param view The view
	 * @param confirm If true, a confirmation dialog will be shown first
	 * @since jEdit 2.7pre2
	 */
	public static void saveAllBuffers(View view, boolean confirm)
	{
		if(confirm)
		{
			int result = GUIUtilities.confirm(view,"saveall",null,
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
			if(result != JOptionPane.YES_OPTION)
				return;
		}

		Buffer buffer = buffersFirst;
		while(buffer != null)
		{
			if(buffer.isDirty())
				buffer.save(view,null,true);
			buffer = buffer.next;
		}
	}

	/**
	 * Reloads all open buffers.
	 * @param view The view
	 * @param confirm If true, a confirmation dialog will be shown first
	 * @since jEdit 2.7pre2
	 */
	public static void reloadAllBuffers(View view, boolean confirm)
	{
		if(confirm)
		{
			int result = GUIUtilities.confirm(view,"reload-all",null,
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
			if(result != JOptionPane.YES_OPTION)
				return;
		}

		Buffer[] buffers = jEdit.getBuffers();
		for(int i = 0; i < buffers.length; i++)
		{
			Buffer buffer = buffers[i];
			buffer.load(view,true);
		}
	}

	/**
	 * Returns the buffer with the specified path name. The path name
	 * must be an absolute, canonical, path.
	 * @param path The path name
	 * @see MiscUtilities#constructPath(String,String)
	 */
	public static Buffer getBuffer(String path)
	{
		boolean caseInsensitiveFilesystem = (File.separatorChar == '\\'
			|| File.separatorChar == ':' /* Windows or MacOS */);

		Buffer buffer = buffersFirst;
		while(buffer != null)
		{
			String _path = buffer.getPath();
			if(caseInsensitiveFilesystem)
			{
				if(_path.equalsIgnoreCase(path))
					return buffer;
			}
			else
			{
				if(_path.equals(path))
					return buffer;
			}
			buffer = buffer.next;
		}

		return null;
	}

	/**
	 * Returns an array of open buffers.
	 */
	public static Buffer[] getBuffers()
	{
		Buffer[] buffers = new Buffer[bufferCount];
		Buffer buffer = buffersFirst;
		for(int i = 0; i < bufferCount; i++)
		{
			buffers[i] = buffer;
			buffer = buffer.next;
		}
		return buffers;
	}

	/**
	 * Returns the number of open buffers.
	 */
	public static int getBufferCount()
	{
		return bufferCount;
	}

	/**
	 * Returns the first buffer.
	 */
	public static Buffer getFirstBuffer()
	{
		return buffersFirst;
	}

	/**
	 * Returns the last buffer.
	 */
	public static Buffer getLastBuffer()
	{
		return buffersLast;
	}

	/**
	 * Returns the current input handler (key binding to action mapping)
	 * @see org.gjt.sp.jedit.gui.InputHandler
	 */
	public static InputHandler getInputHandler()
	{
		return inputHandler;
	}

	/**
	 * Loads all key bindings from the properties.
	 * @since 3.1pre1
	 */
	public static void initKeyBindings()
	{
		inputHandler.removeAllKeyBindings();

		EditAction[] actions = getActions();
		for(int i = 0; i < actions.length; i++)
		{
			EditAction action = actions[i];

			String shortcut1 = jEdit.getProperty(action.getName()
				+ ".shortcut");
			if(shortcut1 != null)
				inputHandler.addKeyBinding(shortcut1,action);

			String shortcut2 = jEdit.getProperty(action.getName()
				+ ".shortcut2");
			if(shortcut2 != null)
				inputHandler.addKeyBinding(shortcut2,action);
		}

		Vector macros = Macros.getMacroList();

		for(int i = 0; i < macros.size(); i++)
		{
			Macros.Macro macro = (Macros.Macro)macros.elementAt(i);
			String shortcut1 = jEdit.getProperty(macro.name + ".shortcut");
			if(shortcut1 != null)
				jEdit.getInputHandler().addKeyBinding(shortcut1,macro.action);

			String shortcut2 = jEdit.getProperty(macro.name + ".shortcut2");
			if(shortcut2 != null)
				jEdit.getInputHandler().addKeyBinding(shortcut2,macro.action);
		}
	}

	/**
	 * Creates a new view of a buffer.
	 * @param view The view from which to take the geometry, buffer and
	 * caret position from
	 * @param buffer The buffer
	 */
	public static View newView(View view, Buffer buffer)
	{
		if(view != null)
		{
			view.showWaitCursor();
			view.saveSplitConfig();
			view.getEditPane().saveCaretInfo();
		}

		View newView = new View(buffer);

		// Do this crap here so that the view is created
		// and added to the list before it is shown
		// (for the sake of plugins that add stuff to views)
		newView.pack();

		if(view != null)
		{
			newView.setSize(view.getSize());
			Point location = view.getLocation();
			location.x += 20;
			location.y += 20;
			newView.setLocation(location);

			view.hideWaitCursor();
		}
		else
		{
			GUIUtilities.loadGeometry(newView,"view");
		}

		addViewToList(newView);
		EditBus.send(new ViewUpdate(newView,ViewUpdate.CREATED));

		newView.show();

		return newView;
	}

	/**
	 * Closes a view. jEdit will exit if this was the last open view.
	 */
	public static void closeView(View view)
	{
		closeView(view,true);
	}

	/**
	 * Returns an array of all open views.
	 */
	public static View[] getViews()
	{
		View[] views = new View[viewCount];
		View view = viewsFirst;
		for(int i = 0; i < viewCount; i++)
		{
			views[i] = view;
			view = view.next;
		}
		return views;
	}

	/**
	 * Returns the number of open views.
	 */
	public static int getViewCount()
	{
		return viewCount;
	}

	/**
	 * Returns the first view.
	 */
	public static View getFirstView()
	{
		return viewsFirst;
	}

	/**
	 * Returns the last view.
	 */
	public static View getLastView()
	{
		return viewsLast;
	}

	/**
	 * Returns the jEdit install directory.
	 */
	public static String getJEditHome()
	{
		return jEditHome;
	}

	/**
	 * Returns the jEdit documentation URL.
	 * @since jEdit 3.1pre5
	 */
	public static String getDocumentationURL()
	{
		return docsHome;
	}
	/**
	 * Returns the user settings directory.
	 */
	public static String getSettingsDirectory()
	{
		return settingsDirectory;
	}

	/**
	 * Saves all user preferences to disk.
	 */
	public static void saveSettings()
	{
		if(settingsDirectory != null)
		{
			// Save the recent file list
			File file = new File(MiscUtilities.constructPath(
				settingsDirectory, "recent"));
			if(file.lastModified() != recentModTime)
			{
				Log.log(Log.WARNING,jEdit.class,file + " changed"
					+ " on disk; will not save recent files");
			}
			else
			{
				BufferHistory.save(file);
			}
			recentModTime = file.lastModified();

			file = new File(MiscUtilities.constructPath(
				settingsDirectory, "history"));
			if(file.lastModified() != historyModTime)
			{
				Log.log(Log.WARNING,jEdit.class,file + " changed"
					+ " on disk; will not save history");
			}
			else
			{
				HistoryModel.saveHistory(file);
			}
			historyModTime = file.lastModified();

			SearchAndReplace.save();
			Abbrevs.save();
			FavoritesVFS.saveFavorites();

			file = new File(MiscUtilities.constructPath(
				settingsDirectory,"properties"));
			if(file.lastModified() != propsModTime)
			{
				Log.log(Log.WARNING,jEdit.class,file + " changed"
					+ " on disk; will not save user properties");
			}
			else
			{
				try
				{
					OutputStream out = new FileOutputStream(file);
					props.save(out,"jEdit properties");
					out.close();
				}
				catch(IOException io)
				{
					Log.log(Log.ERROR,jEdit.class,io);
				}

				propsModTime = file.lastModified();
			}
		}
	}

	/**
	 * Exits cleanly from jEdit, prompting the user if any unsaved files
	 * should be saved first.
	 * @param view The view from which this exit was called
	 * @param reallyExit If background mode is enabled and this parameter
	 * is true, then jEdit will close all open views instead of exiting
	 * entirely.
	 */
	public static void exit(View view, boolean reallyExit)
	{
		// Wait for pending I/O requests
		VFSManager.waitForRequests();

		// Send EditorExitRequested
		EditBus.send(new EditorExitRequested(view));

		// Even if reallyExit is false, we still exit properly
		// if background mode is off
		reallyExit |= !background;

		saveOpenFiles(view);

		// Close all buffers
		if(!closeAllBuffers(view,reallyExit))
			return;

		// If we are running in background mode and
		// reallyExit was not specified, then return here.
		if(!reallyExit)
		{
			// in this case, we can't directly call
			// view.close(); we have to call closeView()
			// for all open views
			view = viewsFirst;
			while(view != null)
			{
				closeView(view,false);
				view = view.next;
			}

			// Save settings in case user kills the backgrounded
			// jEdit process
			saveSettings();

			return;
		}

		// Save view properties here - it unregisters
		// listeners, and we would have problems if the user
		// closed a view but cancelled an unsaved buffer close
		view.close();

		// Stop autosave timer
		Autosave.stop();

		// Stop server
		if(server != null)
			server.stopServer();

		// Stop all plugins
		EditPlugin[] plugins = getPlugins();
		for(int i = 0; i < plugins.length; i++)
		{
			plugins[i].stop();
		}

		// Send EditorExiting
		EditBus.send(new EditorExiting(null));

		// Save settings
		saveSettings();

		// Close activity log stream
		Log.closeStream();

		// Byebye...
		System.exit(0);
	}

	// package-private members

	/**
	 * If buffer sorting is enabled, this repositions the buffer.
	 */
	static void updatePosition(Buffer buffer)
	{
		if(sortBuffers)
		{
			removeBufferFromList(buffer);
			addBufferToList(buffer);
		}
	}

	// private members
	private static String jEditHome;
	private static String docsHome;
	private static String settingsDirectory;
	private static long propsModTime, historyModTime, recentModTime;
	private static Properties defaultProps;
	private static Properties props;
	private static EditServer server;
	private static boolean background;
	private static Hashtable actionHash;
	private static Vector jars;
	private static EditPlugin.JAR plugins; /* plugins without a JAR */
	private static Vector modes;
	private static Vector recent;
	private static boolean saveCaret;
	private static InputHandler inputHandler;

	// buffer link list
	private static boolean sortBuffers;
	private static boolean sortByName;
	private static int bufferCount;
	private static Buffer buffersFirst;
	private static Buffer buffersLast;

	// view link list
	private static int viewCount;
	private static View viewsFirst;
	private static View viewsLast;

	private jEdit() {}

	private static void usage()
	{
		System.out.println("Usage: jedit [<options>] [<files>]");

		System.out.println("	+marker:<marker>: Positions caret"
			+ " at marker <marker>");
		System.out.println("	+line:<line>: Positions caret"
			+ " at line number <line>");
		System.out.println("	--: End of options");
		System.out.println("	-version: Print jEdit version and"
			+ " exit");
		System.out.println("	-usage: Print this message and exit");
		System.out.println("	-norestore: Don't restore previously open files");
		System.out.println("	-noserver: Don't start edit server");
		System.out.println("	-server=<name>: Read/write server"
			+ " info from/to $HOME/.jedit/<name>");

		System.out.println();
		System.out.println("	-nosettings: Don't load user-specific"
			+ " settings");
		System.out.println("	-settings=<path>: Load user-specific"
			+ " settings from <path>");
		System.out.println("	-nosplash: Don't show splash screen");
		System.out.println("	-background: Run in background mode");
		System.out.println("	-nogui: Don't create initial view in background mode");
		System.out.println();
		System.out.println("	-newview: Open new view if connecting to edit server");

		System.out.println();
		System.out.println("To set minimum activity log level,"
			+ " specify a number as the first");
		System.out.println("command line parameter"
			+ " (1-9, 1 = print everything, 9 = fatal errors only)");
		System.out.println();
		System.out.println("Report bugs to Slava Pestov <slava@jedit.org>.");
	}

	private static void version()
	{
		System.out.println("jEdit " + getVersion());
	}

	/**
	 * Initialise various objects, register protocol handlers.
	 */
	private static void initMisc()
	{
		// Add our protocols to java.net.URL's list
		System.getProperties().put("java.protocol.handler.pkgs",
			"org.gjt.sp.jedit.proto|" +
			System.getProperty("java.protocol.handler.pkgs",""));

		inputHandler = new DefaultInputHandler(null);

		// Determine installation directory
		jEditHome = System.getProperty("jedit.home");
		if(jEditHome == null)
		{
			String classpath = System
				.getProperty("java.class.path");
			int index = classpath.toLowerCase()
				.indexOf("jedit.jar");
			int start = classpath.lastIndexOf(File
				.pathSeparator,index) + 1;
			if(index > start)
			{
				jEditHome = classpath.substring(start,
					index - 1);
			}
			else
				jEditHome = System.getProperty("user.dir");
		}

		docsHome = MiscUtilities.constructPath(jEdit.getJEditHome(),"doc");
		docsHome = "file:" + docsHome.replace(File.separatorChar,'/')
			+ File.separatorChar;

		actionHash = new Hashtable();
	}

	/**
	 * Load system properties.
	 */
	private static void initSystemProperties()
	{
		defaultProps = props = new Properties();

		try
		{
			loadProps(jEdit.class.getResourceAsStream(
				"/org/gjt/sp/jedit/jedit.props"),true);
			loadProps(jEdit.class.getResourceAsStream(
				"/org/gjt/sp/jedit/jedit_gui.props"),true);
			loadProps(jEdit.class.getResourceAsStream(
				"/org/gjt/sp/jedit/jedit_keys.props"),true);
		}
		catch(Exception e)
		{
			Log.log(Log.ERROR,jEdit.class,
				"Error while loading system properties!");
			Log.log(Log.ERROR,jEdit.class,
				"One of the following property files could not be loaded:\n"
				+ "- jedit.props\n"
				+ "- jedit_gui.props\n"
				+ "- jedit_keys.props\n"
				+ "jedit.jar is probably corrupt.");
			Log.log(Log.ERROR,jEdit.class,e);
			System.exit(1);
		}
	}

	/**
	 * Load site properties.
	 */
	private static void initSiteProperties()
	{
		// site properties are loaded as default properties, overwriting
		// jEdit's system properties

		String siteSettingsDirectory = MiscUtilities.constructPath(
			jEditHome, "site-props");
		File siteSettings = new File(siteSettingsDirectory);

		if (!(siteSettings.exists() && siteSettings.isDirectory()))
			return;

		String[] snippets = siteSettings.list();
		if (snippets == null)
			return;

		MiscUtilities.quicksort(snippets,
			new MiscUtilities.StringICaseCompare());

		for (int i = 0; i < snippets.length; ++i)
		{
			String snippet = snippets[i];
			if(!snippet.toLowerCase().endsWith(".props"))
				continue;

			try
			{
				String path = MiscUtilities.constructPath(
					siteSettingsDirectory,snippet);
				Log.log(Log.DEBUG,jEdit.class,
					"Loading site snippet: " + path);

				loadProps(new FileInputStream(new File(path)),true);
			}
			catch(FileNotFoundException fnf)
			{
				Log.log(Log.DEBUG,jEdit.class,fnf);
			}
			catch(IOException e)
			{
				Log.log(Log.ERROR,jEdit.class,"Cannot load site snippet "
					+ snippet);
				Log.log(Log.ERROR,jEdit.class,e);
			}
		}
	}

	/**
	 * Load edit modes.
	 */
	private static void initModes()
	{
		/* Try to guess the eventual size to avoid unnecessary
		 * copying */
		modes = new Vector(50);

		// load the global catalog
		loadModeCatalog(MiscUtilities.constructPath(jEditHome,"modes","catalog"));

		// load user catalog
		if(settingsDirectory != null)
		{
			File userModeDir = new File(MiscUtilities.constructPath(
				settingsDirectory,"modes"));
			if(!userModeDir.exists())
				userModeDir.mkdirs();

			File userCatalog = new File(MiscUtilities.constructPath(
				settingsDirectory,"modes","catalog"));
			if(!userCatalog.exists())
			{
				// create dummy catalog
				try
				{
					FileWriter out = new FileWriter(userCatalog);
					out.write(jEdit.getProperty("defaultCatalog"));
					out.close();
				}
				catch(IOException io)
				{
					Log.log(Log.ERROR,jEdit.class,io);
				}
			}

			loadModeCatalog(userCatalog.getPath());
		}
	}

	/**
	 * Load actions.
	 */
	private static void initActions()
	{
		Reader in = new BufferedReader(new InputStreamReader(
			jEdit.class.getResourceAsStream("actions.xml")));
		if(!loadActions("actions.xml",in,false))
			System.exit(1);
	}

	/**
	 * Loads plugins.
	 */
	private static void initPlugins()
	{
		plugins = new EditPlugin.JAR(null,null);
		jars = new Vector();
		loadPlugins(MiscUtilities.constructPath(jEditHome,"jars"));
		if(settingsDirectory != null)
		{
			File jarsDirectory = new File(settingsDirectory,"jars");
			if(!jarsDirectory.exists())
				jarsDirectory.mkdir();
			loadPlugins(jarsDirectory.getPath());
		}
	}

	/**
	 * Loads user properties.
	 */
	private static void initUserProperties()
	{
		props = new Properties(defaultProps);

		if(settingsDirectory != null)
		{
			File file = new File(MiscUtilities.constructPath(
				settingsDirectory,"properties"));
			propsModTime = file.lastModified();

			try
			{
				loadProps(new FileInputStream(file),false);
			}
			catch(FileNotFoundException fnf)
			{
				Log.log(Log.DEBUG,jEdit.class,fnf);
			}
			catch(IOException e)
			{
				Log.log(Log.ERROR,jEdit.class,e);
			}
		}
	}

	/**
	 * Sets the Swing look and feel.
	 */
	private static void initPLAF()
	{
		String lf = getProperty("lookAndFeel");
		try
		{
			if(lf != null && lf.length() != 0)
				UIManager.setLookAndFeel(lf);
		}
		catch(Exception e)
		{
			Log.log(Log.ERROR,jEdit.class,e);
		}
	}

	private static void getNotLoadedPluginJARs(Vector returnValue,
		String dir, String[] list)
	{
loop:		for(int i = 0; i < list.length; i++)
		{
			String name = list[i];
			if(!name.toLowerCase().endsWith(".jar"))
				continue loop;

			String path = MiscUtilities.constructPath(dir,name);

			for(int j = 0; j < jars.size(); j++)
			{
				EditPlugin.JAR jar = (EditPlugin.JAR)
					jars.elementAt(j);
				String jarPath = jar.getPath();
				String jarName = MiscUtilities.getFileName(jarPath);

				if(path.equals(jarPath))
					continue loop;
				else if(!new File(jarPath).exists()
					&& name.equals(jarName))
					continue loop;
			}

			returnValue.addElement(path);
		}
	}

	private static void gotoMarker(Buffer buffer, String marker)
	{
		VFSManager.runInAWTThread(new GotoMarkerSafely(buffer,marker));
	}

	static class GotoMarkerSafely implements Runnable
	{
		Buffer buffer;
		String marker;

		GotoMarkerSafely(Buffer buffer, String marker)
		{
			this.buffer = buffer;
			this.marker = marker;
		}

		public void run()
		{
			int pos;

			// Handle line number
			if(marker.startsWith("+line:"))
			{
				try
				{
					int line = Integer.parseInt(marker.substring(6));
					Element lineElement = buffer.getDefaultRootElement()
						.getElement(line - 1);
					pos = lineElement.getStartOffset();
				}
				catch(Exception e)
				{
					return;
				}
			}
			// Handle marker
			else if(marker.startsWith("+marker:"))
			{
				if(marker.length() != 9)
					return;

				Marker m = buffer.getMarker(marker.charAt(8));
				if(m == null)
					return;
				pos = m.getPosition();
			}
			// Can't happen
			else
				throw new InternalError();

			buffer.putProperty(Buffer.CARET,new Integer(pos));
			buffer.getDocumentProperties().remove(Buffer.SCROLL_HORIZ);
			buffer.getDocumentProperties().remove(Buffer.SCROLL_VERT);
		}
	}

	private static void addBufferToList(Buffer buffer)
	{
		// if only one, clean, 'untitled' buffer is open, we
		// replace it
		if(viewCount <= 1 && buffersFirst != null
			&& buffersFirst == buffersLast
			&& buffersFirst.isUntitled()
			&& !buffersFirst.isDirty())
		{
			Buffer oldBuffersFirst = buffersFirst;
			buffersFirst = buffersLast = buffer;
			EditBus.send(new BufferUpdate(oldBuffersFirst,null,
				BufferUpdate.CLOSED));
			return;
		}

		bufferCount++;

		if(buffersFirst == null)
		{
			buffersFirst = buffersLast = buffer;
			return;
		}
		else if(sortBuffers)
		{
			String name1 = (sortByName ? buffer.getName()
				: buffer.getPath()).toLowerCase();

			Buffer _buffer = buffersFirst;
			while(_buffer != null)
			{
				String name2 = (sortByName ? _buffer.getName()
					: _buffer.getPath()).toLowerCase();
				if(name1.compareTo(name2) <= 0)
				{
					buffer.next = _buffer;
					buffer.prev = _buffer.prev;
					_buffer.prev = buffer;
					if(_buffer != buffersFirst)
						buffer.prev.next = buffer;
					else
						buffersFirst = buffer;
					return;
				}

				_buffer = _buffer.next;
			}
		}

		buffer.prev = buffersLast;
		buffersLast.next = buffer;
		buffersLast = buffer;
	}

	private static void removeBufferFromList(Buffer buffer)
	{
		bufferCount--;

		if(buffer == buffersFirst && buffer == buffersLast)
		{
			buffersFirst = buffersLast = null;
			return;
		}

		if(buffer == buffersFirst)
		{
			buffersFirst = buffer.next;
			buffer.next.prev = null;
		}
		else
		{
			buffer.prev.next = buffer.next;
		}

		if(buffer == buffersLast)
		{
			buffersLast = buffersLast.prev;
			buffer.prev.next = null;
		}
		else
		{
			buffer.next.prev = buffer.prev;
		}

		// fixes the hang that can occur if we 'save as' to a new
		// filename which requires re-sorting
		buffer.next = buffer.prev = null;
	}

	private static void addViewToList(View view)
	{
		viewCount++;

		if(viewsFirst == null)
			viewsFirst = viewsLast = view;
		else
		{
			view.prev = viewsLast;
			viewsLast.next = view;
			viewsLast = view;
		}
	}

	private static void removeViewFromList(View view)
	{
		viewCount--;

		if(viewsFirst == viewsLast)
		{
			viewsFirst = viewsLast = null;
			return;
		}

		if(view == viewsFirst)
		{
			viewsFirst = view.next;
			view.next.prev = null;
		}
		else
		{
			view.prev.next = view.next;
		}

		if(view == viewsLast)
		{
			viewsLast = viewsLast.prev;
			view.prev.next = null;
		}
		else
		{
			view.next.prev = view.prev;
		}
	}

	/**
	 * closeView() used by exit().
	 */
	private static void closeView(View view, boolean callExit)
	{
		if(viewsFirst == viewsLast && callExit)
			exit(view,false); /* exit does editor event & save */
		else
		{
			EditBus.send(new ViewUpdate(view,ViewUpdate.CLOSED));

			view.close();
			removeViewFromList(view);
		}
	}
}
