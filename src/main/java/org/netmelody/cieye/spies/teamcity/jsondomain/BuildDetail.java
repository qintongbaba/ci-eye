package org.netmelody.cieye.spies.teamcity.jsondomain;

import java.util.Date;

import org.netmelody.cieye.core.domain.Status;

public final class BuildDetail {
    public long id;
    public String number;
    public String status;
    public String href;
    public String webUrl;
    public boolean personal;
    public boolean history;
    public boolean pinned;
    public String statusText;
    //buildType
    public Date startDate;
    public Date finishDate;
    //agent
    public Comment comment;
    //tags
    //properties
    //revisions
    public ChangesHref changes;
    //relatedIssues
    
    public Status status() {
        if (status == null || "SUCCESS".equals(status)) {
            return Status.GREEN;
        }
        if (comment != null && comment.text != null && !comment.text.isEmpty()) {
            return Status.UNDER_INVESTIGATION;
        }
        return Status.BROKEN;
    }

    public long startDateTime() {
        return (null == startDate) ? 0L : startDate.getTime();
    }
}