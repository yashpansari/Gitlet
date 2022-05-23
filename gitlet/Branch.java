package gitlet;

import java.io.Serializable;

/** Contains all the useful information to identify a branch.
 *  @author Yash Pansari
 */
public class Branch implements Serializable {

    public Branch(String branchName, String rootId,
                  Commit head) {
        _branchName = branchName;
        _rootId = rootId;
        _head = head;
    }

    public void setHead(Commit head) {
        _head = head;
    }

    public String getBranchName() {
        return _branchName;
    }

    public String getRootId() {
        return _rootId;
    }

    public Commit getHead() {
        return _head;
    }

    /** Contains all the useful information about a commit operation.
     */
    private String _branchName;

    /** CommitID of the first commit that made the branch.
     */
    private String _rootId;

    /** Latest commit in the branch.
     */
    private Commit _head;
}
