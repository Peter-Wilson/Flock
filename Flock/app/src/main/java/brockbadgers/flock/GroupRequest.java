package brockbadgers.flock;

import android.content.Context;

/**
 * Created by Peter on 9/17/2016.
 */
public class GroupRequest {

    String leaderName;
    String[] requestIds;
    Context ctx;

    public GroupRequest(String leaderName, String[] requestIds, Context ctx) {
        this.leaderName = leaderName;
        this.requestIds = requestIds;
        this.ctx = ctx;
    }

    public Context getCtx() {
        return ctx;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public String[] getRequestIds() {
        return requestIds;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public void setRequestIds(String[] requestIds) {
        this.requestIds = requestIds;
    }
}
