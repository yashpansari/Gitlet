package gitlet;
import java.util.HashMap;
import java.util.Date;
import java.io.Serializable;
import java.util.HashSet;

/** Contains all the useful information about a commit operation.
 *  @author Yash Pansari
 */
public class Commit implements Serializable {

    public Commit(Commit parent, HashMap<String, byte[]> tracked,
                  HashSet<String> removed, String message, String branch) {
        _parent = parent;
        _parent2 = null;
        _tracked = tracked;
        _message = message;
        _branchName = branch;
        if (parent == null) {
            _timeStamp = "Wed Dec 31 16:00:00 1969 -0800";
        } else {
            _timeStamp = (new Date()).toString();
            _timeStamp = _timeStamp.substring(0, 2 * 10) + "2022 -0700";
            for (String key : parent.getBlobs().keySet()) {
                byte[] value = parent.getBlobs().get(key);
                if (!(tracked.containsKey(key)
                        && value != tracked.get(key))) {
                    _tracked.put(key, value);
                }
            }
        }
        if (removed != null) {
            for (String temp : removed) {
                _tracked.remove(temp);
            }
        }
        _commitID = Utils.sha1(Utils.serialize(this));
    }

    public Commit(Commit parent1, Commit parent2,
                  HashMap<String, byte[]> tracked, HashSet<String> removed,
                  String message, String branch) {
        _parent = parent1;
        _parent2 = parent2;
        _tracked = tracked;
        _message = message;
        _branchName = branch;
        _timeStamp = (new Date()).toString();
        _timeStamp = _timeStamp.substring(0, 2 * 10) + "2022 -0700";
        for (String key : _parent.getBlobs().keySet()) {
            byte[] value = _parent.getBlobs().get(key);
            if (!(tracked.containsKey(key)
                    && value != tracked.get(key))) {
                _tracked.put(key, value);
            }
        }
        if (removed != null) {
            for (String temp : removed) {
                _tracked.remove(temp);
            }
        }
        _commitID = Utils.sha1(Utils.serialize(this));
    }

    @Override
    public String toString() {
        String output = "===\n";
        output += "commit " + _commitID + "\n";
        output += "Date: " + _timeStamp + "\n";
        output += _message + "\n";
        return output;
    }

    public String getID() {
        return _commitID;
    }

    public Commit getParent() {
        return _parent;
    }

    public Commit getParent2() {
        return _parent2;
    }

    public HashMap<String, byte[]> getBlobs() {
        return _tracked;
    }

    public String getMessage() {
        return _message;
    }

    public String getBranchName() {
        return _branchName;
    }

    public String getTime() {
        return _timeStamp;
    }

    /** Unique id for each commit.
     */
    private String _commitID;

    /** Parent commit.
     */
    private Commit _parent;

    /** Parent commit.
     */
    private Commit _parent2;

    /** Tracked files of commit.
     */
    private HashMap<String, byte[]> _tracked;

    /** Commit message.
     */
    private String _message;

    /** Branch name.
     */
    private String _branchName;

    /** Time at which commit was made.
     */
    private String _timeStamp;

}
