package gitlet;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Yash Pansari
 *  Design doc collaborated on with my lab partner, Alexander Ge. Thus, the
 *  outline of our projects may be similar but the code is still independent.
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (check(args)) {
            if (args[0].equals("init")) {
                check(1, args);
                new GitletRepo(true);
                return;
            } else {
                GitletRepo r = new GitletRepo(false);
                if (args[0].equals("add")) {
                    check(2, args);
                    r.add(args[1]);
                } else if (args[0].equals("rm")) {
                    check(2, args);
                    r.rm(args[1]);
                } else if (args[0].equals("commit")) {
                    check(2, args);
                    if (args.length <= 1 || args[1].equals("")) {
                        System.out.println("Please enter a commit message.");
                    } else {
                        r.commit(args[1]);
                    }
                } else if (args[0].equals("log")) {
                    check(1, args);
                    r.log();
                } else if (args[0].equals("global-log")) {
                    check(1, args);
                    r.globalLog();
                } else if (args[0].equals("checkout")) {
                    check(2, 4, args, true);
                    r.checkout(args);
                } else if (args[0].equals("status")) {
                    check(1, args);
                    System.out.println(r.status());
                } else if (args[0].equals("find")) {
                    check(2, args);
                    System.out.println(r.find(args[1]));
                } else if (args[0].equals("branch")) {
                    check(2, args);
                    r.branch(args[1]);
                } else if (args[0].equals("cb")) {
                    check(1, args);
                    r.cb();
                } else if (args[0].equals("rm-branch")) {
                    check(2, args);
                    r.rmBranch(args[1]);
                } else if (args[0].equals("reset")) {
                    check(2, args);
                    r.reset(args[1]);
                } else if (args[0].equals("merge")) {
                    check(2, args);
                    r.merge(args[1]);
                } else {
                    System.out.println("No command with that name exists.");
                }
            }
        }
    }
    public static boolean check(String[] ar) {
        if (ar.length == 0) {
            System.out.println("Please enter a command.");
            return false;
        }
        return true;
    }
    public static void check(int val, String[] ar) {
        if (val != ar.length) {
            System.out.println("Incorrect operands.");
        }
    }
    public static void check(int min, int max, String[] ar) {
        if (min > ar.length
                || max < ar.length) {
            System.out.println("Incorrect operands.");
        }
    }
    public static void check(int min, int max, String[] ar, boolean g) {
        if (min > ar.length
                || max < ar.length
                || (ar.length == 4
                && !ar[2].equals("--"))) {
            System.out.println("Incorrect operands.");
        }
    }
}
