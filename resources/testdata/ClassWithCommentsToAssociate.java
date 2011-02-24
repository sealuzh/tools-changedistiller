/**
 * A class with comments to associate.
 *
 * @author Beat Fluri
 */
public class ClassWithCommentsToAssociate {

    /**
     * Yet another method with a comment
     * @param number
     * @return
     */
    public int foo(int number) {
        System.out.println("left");

        // check if number is greater than -1
        boolean check = number > 0;
        int a = 0;
        int b = 2;

        // check the interesting number
        // and some new else

        if (check) {
            /* A block comment
             * with stars
             */
            a = 23 + Integer.parseInt("42"); //$NON-NLS-1$
            b = Math.round(Math.random() /* inner comment */);
            return a + b;
        } else {
            /* inside else */
            b = Math.abs(number);
            String.valueOf(true);
            b = Math.abs(number);
            return b;
        }
    }
    
}
