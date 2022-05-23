package gitlet;

import ucb.junit.textui;
import org.junit.Test;
import java.io.File;
import java.util.HashMap;

import static org.junit.Assert.*;

/** The suite of all JUnit tests for the gitlet package.
 *  @author Yash Pansari
 */
public class UnitTest {

    /** Run the JUnit tests in the loa package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        System.exit(textui.runClasses(UnitTest.class));
    }

    /** A dummy test to avoid complaint. */
    @Test
    @SuppressWarnings({"unchecked", "[unchecked]"})
    public void initTest() {
        File g = (new File(".gitlet"));
        if (g.exists()) {
            for (File file : g.listFiles()) {
                file.delete();
            }
            g.delete();
        }
        Main.main("init");
        assertTrue(g.exists());

        for (File file : g.listFiles()) {
            file.delete();
        }
        g.delete();
    }

    @Test
    @SuppressWarnings({"unchecked", "[unchecked]"})
    public void addrmTest() {
        File g = (new File(".gitlet"));
        if (g.exists()) {
            for (File file : g.listFiles()) {
                file.delete();
            }
            g.delete();
        }
        Main.main("init");
        assertTrue(g.exists());
        Main.main("add", "testing/src/wug.txt");
        HashMap<String, byte[]> s = Utils.readObject(
                new File(".gitlet/stage.txt"), HashMap.class);
        System.out.println(s);
        Main.main("add", "testing/src/wug.txt");
        s = Utils.readObject(new File(".gitlet/stage.txt"), HashMap.class);
        System.out.println(s);
        Main.main("add", "testing/src/notwug.txt");
        s = Utils.readObject(new File(".gitlet/stage.txt"), HashMap.class);
        System.out.println(s);
        Main.main("add", "testing/src/mug.txt");
        Main.main("rm", "testing/src/wug.txt");
        s = Utils.readObject(new File(".gitlet/stage.txt"), HashMap.class);
        System.out.println(s);
        for (File file : g.listFiles()) {
            file.delete();
        }
        g.delete();
    }

    @Test
    @SuppressWarnings({"unchecked", "[unchecked]"})
    public void commitLogTest() {
        File g = (new File(".gitlet"));
        if (g.exists()) {
            for (File file : g.listFiles()) {
                file.delete();
            }
            g.delete();
        }
        Main.main("init");
        assertTrue(g.exists());
        Main.main("commit", "wug created");
        Main.main("add", "testing/src/wug.txt");
        HashMap<String, byte[]> s = Utils.readObject(
                new File(".gitlet/stage.txt"), HashMap.class);
        System.out.println(s);
        Main.main("commit", "wug created");
        s = Utils.readObject(new File(".gitlet/stage.txt"), HashMap.class);
        System.out.println(s);
        Main.main("log");
        for (File file : g.listFiles()) {
            file.delete();
        }
        g.delete();
    }

    @Test
    @SuppressWarnings({"unchecked", "[unchecked]"})
    public void commitGLTest() {
        File g = (new File(".gitlet"));
        if (g.exists()) {
            for (File file : g.listFiles()) {
                file.delete();
            }
            g.delete();
        }
        Main.main("init");
        assertTrue(g.exists());
        Main.main("add", "testing/src/wug.txt");
        HashMap<String, byte[]> s = Utils.readObject(
                new File(".gitlet/stage.txt"), HashMap.class);
        System.out.println(s);
        Main.main("commit", "wug created");
        s = Utils.readObject(new File(".gitlet/stage.txt"), HashMap.class);
        System.out.println(s);
        Main.main("global-log");
        for (File file : g.listFiles()) {
            file.delete();
        }
        g.delete();
    }


}


