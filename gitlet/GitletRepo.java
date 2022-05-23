package gitlet;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Yash Pansari
 */
public class GitletRepo {

    @SuppressWarnings("unchecked")
    public GitletRepo(Boolean init) {
        if (!init) {
            if (!(new File("./.gitlet")).exists()) {
                System.out.println("Not in an initialized Gitlet directory.");
                System.exit(0);
            } else {
                File lineage = new File("./.gitlet/lineage.txt");
                _lineage = Utils.readObject(lineage, HashMap.class);
                File stage = new File("./.gitlet/stage.txt");
                _stagedFiles = Utils.readObject(stage, HashMap.class);
                File removed = new File("./.gitlet/removed.txt");
                _removedFiles = Utils.readObject(removed, HashSet.class);
                File branches = new File("./.gitlet/branches.txt");
                _branches = Utils.readObject(branches, LinkedHashMap.class);
                File currentBranch = new File("./.gitlet/currentbranch.txt");
                _currentBranch = Utils.readObject(currentBranch, Branch.class);
                _current = _currentBranch.getHead();
            }
        } else {
            if ((new File("./.gitlet")).exists()) {
                System.out.println("A Gitlet version-control system "
                        + "already exists in the current directory.");
            } else {
                (new File("./.gitlet")).mkdir();
                _stagedFiles = new HashMap<String, byte[]>();
                _removedFiles = new HashSet<String>();
                _lineage = new HashMap<String, Commit>();
                _current = new Commit(null,
                        _stagedFiles,
                        _removedFiles,
                        "initial commit",
                        "master");
                _lineage.put(_current.getID(), _current);
                _branches = new LinkedHashMap<String, Branch>();
                _currentBranch = new Branch("master",
                        _current.getID(), _current);
                _branches.put(_currentBranch.getBranchName(),
                        _currentBranch);
                Utils.writeObject(new File("./.gitlet/lineage.txt"),
                        _lineage);
                Utils.writeObject(new File("./.gitlet/stage.txt"),
                        _stagedFiles);
                Utils.writeObject(new File("./.gitlet/removed.txt"),
                        _removedFiles);
                Utils.writeObject(new File(".gitlet/branches.txt"),
                        _branches);
                Utils.writeObject(new File(".gitlet/currentbranch.txt"),
                        _currentBranch);
            }
        }
    }

    public void add(String fileName) {
        if ((new File(fileName)).exists()) {
            byte[] fBlob = Utils.readContents(new File(fileName));
            if (_removedFiles.contains(fileName)) {
                _removedFiles.remove(fileName);
                Utils.writeObject(new File("./.gitlet/removed.txt"),
                        _removedFiles);
            }
            if (!(_stagedFiles.containsKey(fileName)
                    && !Utils.sha1(_stagedFiles.get(fileName)).equals(
                    Utils.sha1(fBlob)))
                    && !(_current.getBlobs().containsKey(fileName)
                    && Utils.sha1(_current.getBlobs().get(fileName)).equals(
                    Utils.sha1(fBlob)))) {
                _stagedFiles.put(fileName, fBlob);
                Utils.writeObject(new File("./.gitlet/stage.txt"),
                        _stagedFiles);
            } else {
                _stagedFiles.replace(fileName, fBlob);
                if (_current.getBlobs().containsKey(fileName)
                        && _current.getBlobs().get(fileName) == fBlob) {
                    _stagedFiles.remove(fileName);
                }
                Utils.writeObject(new File("./.gitlet/stage.txt"),
                        _stagedFiles);
            }
        } else {
            System.out.println("File does not exist.");
        }
    }

    public void rm(String fileName) {
        if (!_stagedFiles.containsKey(fileName)
                && !_current.getBlobs().containsKey(fileName)) {
            System.out.println("No reason to remove the file.");
        } else {
            if (_stagedFiles.containsKey(fileName)) {
                _stagedFiles.remove(fileName);
                Utils.writeObject(new File("./.gitlet/stage.txt"),
                        _stagedFiles);
            }
            if (_current.getBlobs().containsKey(fileName)) {
                File file = new File(fileName);
                _removedFiles.add(fileName);
                Utils.writeObject(new File("./.gitlet/removed.txt"),
                        _removedFiles);
                if (file.exists()) {
                    file.delete();
                }
            }

        }
    }

    public void commit(String message) {
        if (_stagedFiles.size() + _removedFiles.size() == 0) {
            System.out.println("No changes added to the commit.");
        } else {
            _current = new Commit(_current,
                    _stagedFiles, _removedFiles, message,
                    _currentBranch.getBranchName());
            _lineage.put(_current.getID(), _current);
            Utils.writeObject(new File("./.gitlet/lineage.txt"),
                    _lineage);
            _branches.get(
                    _currentBranch.getBranchName()).setHead(_current);
            _currentBranch.setHead(_current);
            Utils.writeObject(new File("./.gitlet/stage.txt"),
                    new HashMap<String, byte[]>());
            Utils.writeObject(new File("./.gitlet/removed.txt"),
                    new HashSet<String>());
            Utils.writeObject(new File("./.gitlet/currentbranch.txt"),
                    _currentBranch);
            Utils.writeObject(new File("./.gitlet/branches.txt"),
                    _branches);
        }
    }

    public void cb() {
        System.out.println(_currentBranch.getHead());
        for (String temp : _branches.keySet()) {
            System.out.println(temp);
            System.out.println(_branches.get(temp).getHead());
        }
    }

    public void log() {
        Commit temp = _current;
        while (temp != null) {
            System.out.println(temp);
            temp = temp.getParent();
        }
    }

    public void globalLog() {
        for (String i: _lineage.keySet()) {
            System.out.println(_lineage.get(i));
        }
    }

    public void checkout(String id, String fileName) {
        if (id.length() < 4 * 10) {
            for (String tid : _lineage.keySet()) {
                if (tid.substring(0,
                        id.length()).equals(id)) {
                    id = tid;
                }
            }
        }
        if (_lineage.containsKey(id)) {
            Commit temp = _lineage.get(id);
            if (temp.getBlobs().containsKey(fileName)) {
                byte[] blob = temp.getBlobs().get(fileName);
                Utils.writeContents(new File(fileName),
                        blob);
            } else {
                System.out.println("File does not exist in that commit.");
            }
        } else {
            System.out.println("No commit with that id exists.");
        }
    }

    public String status() {
        StringBuffer out = new StringBuffer();
        out.append("=== Branches ===\n");
        for (String name : _branches.keySet()) {
            if (name.equals(_currentBranch.getBranchName())) {
                out.append("*");
            }
            out.append(name + "\n");
        }
        out.append("\n");
        out.append("=== Staged Files ===\n");
        for (String name : _stagedFiles.keySet()) {
            out.append(name + "\n");
        }
        out.append("\n");
        out.append("=== Removed Files ===");
        out.append("\n");
        for (String name : _removedFiles) {
            out.append(name + "\n");
        }
        out.append("\n");
        out.append("=== Modifications Not Staged For Commit ===\n");
        HashMap<String, byte[]> blobs = new Commit(_current,
                _stagedFiles, _removedFiles, "temp checker",
                _currentBranch.getBranchName()).getBlobs();
        for (String name : blobs.keySet()) {
            if (!(new File(name)).exists()) {
                out.append(name + " (deleted)\n");
            } else if (
                    !Utils.sha1(
                            Utils.readContents(new File(name))).equals(
                                    Utils.sha1(blobs.get(name)))) {
                out.append(name + " (modified)\n");
            }
        }
        out.append("\n");
        out.append("=== Untracked Files ===\n");
        for (String name : Utils.plainFilenamesIn(".")) {
            if (!blobs.containsKey(name)) {
                out.append(name + "\n");
            }
        }
        return out.toString();
    }

    public String find(String message) {
        StringBuffer out = new StringBuffer();
        for (String id : _lineage.keySet()) {
            if (_lineage.get(id).getMessage().equals(
                    message)) {
                out.append(id + "\n");
            }
        }
        if (out.length() == 0) {
            return "Found no commit with that message.";
        }
        return out.toString();
    }

    public void branch(String name) {
        if (_branches.containsKey(name)) {
            System.out.println("A branch with that name already exists.");
        } else {
            _branches.put(name,
                    new Branch(name, _current.getID(), _current));
            Utils.writeObject(new File(".gitlet/branches.txt"),
                    _branches);
        }
    }
    public void checkout(String name) {
        if (!_branches.containsKey(name)) {
            System.out.println("No such branch exists.");
        } else if (_currentBranch.getBranchName().equals(name)) {
            System.out.println(
                    "No need to checkout the current branch.");
        } else {
            _currentBranch = _branches.get(name);
            Commit newCurrent = _currentBranch.getHead();
            HashMap<String, byte[]> blobs = _current.getBlobs();
            HashMap<String, byte[]> newBlobs = newCurrent.getBlobs();
            List<String> files = Utils.plainFilenamesIn(".");
            for (String temp : files) {
                if (!blobs.containsKey(temp)
                        && (!newBlobs.containsKey(temp)
                        || !Utils.sha1(newBlobs.get(temp)).equals(
                                Utils.sha1(
                                        Utils.readContents(new File(temp)))))) {
                    System.out.println("There is an untracked file"
                            + " in the way; delete it, "
                            + "or add and commit it first.");
                    return;
                }
            }
            _current = newCurrent;
            for (String temp : files) {
                new File(temp).delete();
            }
            for (String temp : newBlobs.keySet()) {
                Utils.writeContents(new File(temp),
                        newBlobs.get(temp));
            }
            Utils.writeObject(new File(".gitlet/currentbranch.txt"),
                    _currentBranch);
        }
    }
    public void checkout(String[] args) {
        if (args.length == 2) {
            checkout(args[1]);
        } else if (args.length == 3) {
            checkout(getCurrent().getID(), args[2]);
        } else {
            checkout(args[1], args[3]);
        }
    }
    public void rmBranch(String name) {
        if (!_branches.containsKey(name)) {
            System.out.println("A branch with that name does not exist.");
        } else if (_currentBranch.getBranchName().equals(name)) {
            System.out.println("Cannot remove the current branch.");
        } else {
            _branches.remove(name);
            Utils.writeObject(new File("./.gitlet/branches.txt"),
                    _branches);
        }
    }
    public void reset(String id) {
        if (id.length() < 4 * 10) {
            for (String tid : _lineage.keySet()) {
                if (tid.substring(0,
                        id.length()).equals(id)) {
                    id = tid;
                }
            }
        }
        if (!_lineage.containsKey(id)) {
            System.out.println("No commit with that id exists.");
        } else {
            Commit newCurrent = _lineage.get(id);
            Commit tCommit = new Commit(_current,
                    _stagedFiles, _removedFiles,
                    "temp", _currentBranch.getBranchName());
            HashMap<String, byte[]> blobs = tCommit.getBlobs();
            HashMap<String, byte[]> newBlobs = newCurrent.getBlobs();
            List<String> files = Utils.plainFilenamesIn(".");
            for (String temp : files) {
                if (!blobs.containsKey(temp)
                        && (!newBlobs.containsKey(temp)
                        || !Utils.sha1(newBlobs.get(temp)).equals(
                        Utils.sha1(
                                Utils.readContents(new File(temp)))))) {
                    System.out.println("There is an untracked file"
                            + " in the way; delete it, "
                            + "or add and commit it first.");
                    return;
                }
            }
            _current = newCurrent;
            for (String temp : files) {
                (new File(temp)).delete();
            }
            for (String temp : newBlobs.keySet()) {
                Utils.writeContents(new File(temp),
                        newBlobs.get(temp));
            }
            _currentBranch = _branches.get(_current.getBranchName());
            _currentBranch.setHead(_current);
            Utils.writeObject(new File(".gitlet/currentbranch.txt"),
                    _currentBranch);
            Utils.writeObject(new File(".gitlet/branches.txt"),
                    _branches);
            Utils.writeObject(new File("./.gitlet/stage.txt"),
                    new HashMap<String, byte[]>());
            Utils.writeObject(new File("./.gitlet/removed.txt"),
                    new HashSet<String>());
        }
    }

    public void merge(String b2name) {
        if (check(b2name)) {
            return;
        }
        Branch b1 = _currentBranch;
        Branch b2 = _branches.get(b2name);
        Commit splitPoint = splitPoint(b1, b2);
        if (check(b1.getHead(),
                b2.getHead(), splitPoint)) {
            return;
        }
        HashMap<String, byte[]> curBlobs = b1.getHead().getBlobs();
        HashMap<String, byte[]> givBlobs = b2.getHead().getBlobs();
        HashMap<String, byte[]> splitBlobs = splitPoint.getBlobs();
        for (String temp : Utils.plainFilenamesIn(".")) {
            if (givBlobs.containsKey(temp)
                    && !curBlobs.containsKey(temp)) {
                System.out.println("There is an "
                        + "untracked file in the way; "
                        + "delete it, or add and commit it first.");
                return;
            }
        }
        case1(curBlobs, givBlobs, splitBlobs);
        for (String temp : givBlobs.keySet()) {
            if (!splitBlobs.containsKey(temp)
                    && !curBlobs.containsKey(temp)) {
                Utils.writeContents(new File(temp),
                        givBlobs.get(temp));
                add(temp);
            }
        }
        mcommit("Merged "
                + b2.getBranchName() + " into "
                + b1.getBranchName() + ".",
                b1.getHead(), b2.getHead());
    }

    public void mcommit(String message,
                       Commit p1, Commit p2) {
        if (_stagedFiles.size() + _removedFiles.size() == 0) {
            System.out.println("No changes added to the commit.");
        } else {
            _current = new Commit(p1, p2,
                    _stagedFiles, _removedFiles, message,
                    _currentBranch.getBranchName());
            _lineage.put(_current.getID(), _current);
            Utils.writeObject(new File("./.gitlet/lineage.txt"),
                    _lineage);
            _branches.get(
                    _currentBranch.getBranchName()).setHead(_current);
            _currentBranch.setHead(_current);
            Utils.writeObject(new File("./.gitlet/stage.txt"),
                    new HashMap<String, byte[]>());
            Utils.writeObject(new File("./.gitlet/removed.txt"),
                    new HashSet<String>());
            Utils.writeObject(new File("./.gitlet/currentbranch.txt"),
                    _currentBranch);
            Utils.writeObject(new File("./.gitlet/branches.txt"),
                    _branches);
        }
    }

    public void case1(HashMap<String, byte[]> curBlobs,
                      HashMap<String, byte[]> givBlobs,
                      HashMap<String, byte[]> splitBlobs) {
        for (String temp : splitBlobs.keySet()) {
            if (curBlobs.containsKey(temp)
                    && givBlobs.containsKey(temp)
                    && !Utils.sha1(splitBlobs.get(temp)).equals(
                    Utils.sha1(givBlobs.get(temp)))
                    && Utils.sha1(splitBlobs.get(temp)).equals(
                    Utils.sha1(curBlobs.get(temp)))) {
                Utils.writeContents(new File(temp),
                        givBlobs.get(temp));
                add(temp);
            }
            if (curBlobs.containsKey(temp)
                    && Utils.sha1(splitBlobs.get(temp)).equals(
                    Utils.sha1(curBlobs.get(temp)))
                    && !givBlobs.containsKey(temp)) {
                rm(temp);
            }
        }
    }

    public boolean check(Commit c1, Commit c2, Commit split) {
        if (c2.equals(split)) {
            System.out.println("Given branch is an"
                    + " ancestor of the current branch.");
            return true;
        }
        if (c1.equals(split)) {
            checkout(c2.getBranchName());
            System.out.println(split);
            System.out.println(
                    "Current branch fast-forwarded.");
            return true;
        }
        return false;
    }

    public boolean check(String b2name) {
        if (_stagedFiles.size()
                + _removedFiles.size() != 0) {
            System.out.println("You have uncommitted changes.");
            return true;
        }
        if (!_branches.containsKey(b2name)) {
            System.out.println("A branch with that name does not exist.");
            return true;
        }
        if (_currentBranch.getBranchName().equals(
                b2name)) {
            System.out.println("Cannot merge a branch with itself.");
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public Commit splitPoint(Branch b1, Branch b2) {
        ArrayList lineage1 = new ArrayList<Commit>();
        ArrayList lineage2 = new ArrayList<Commit>();
        Commit t1 = b1.getHead();
        Commit t2 = b2.getHead();
        while (t1 != null) {
            lineage1.add(t1);
            t1 = t1.getParent();
        }
        while (t2 != null) {
            lineage2.add(t2);
            t2 = t2.getParent();
        }
        int len = java.lang.Math.min(
                lineage1.size(), lineage2.size());
        for (int i = 0; i < len; i++) {
            if (((Commit) lineage1.get(i)).getID().equals(
                    ((Commit) lineage2.get(i)).getID())) {
                return (Commit) lineage1.get(i);
            }
        }
        return _lineage.get(
                "4f48343edf2e0c3c14a0aa34998e11b8a5d747dd");
    }

    public Commit getCurrent() {
        return _current;
    }

    /** Head commit of master branch.
     */
    private Commit _current;

    /** Commit Tree.
     */
    private HashMap<String, Commit> _lineage;

    /** Files staged for addition.
     */
    private HashMap<String, byte[]> _stagedFiles;

    /** Files staged for removal.
     */
    private HashSet<String> _removedFiles;

    /** All of the branches.
     */
    private LinkedHashMap<String, Branch> _branches;

    /** The current branch.
     */
    private Branch _currentBranch;
}
