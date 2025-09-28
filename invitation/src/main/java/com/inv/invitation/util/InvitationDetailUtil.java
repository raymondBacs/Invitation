package com.inv.invitation.util;

import java.time.LocalDateTime;

import com.inv.invitation.model.InvitationDetail;

public class InvitationDetailUtil {
	
	public boolean isEventDone(InvitationDetail detail) {
        if (detail.getEvent_date() == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(detail.getEvent_date());
    }

    public boolean isEventUpcoming(InvitationDetail detail) {
        if (detail.getEvent_date() == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(detail.getEvent_date());
    }
    
    public boolean isEventHappeningNow(InvitationDetail detail) {
        if (detail.getEvent_date() == null || detail.getEventEndTime() == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return (now.isEqual(detail.getEvent_date()) || now.isAfter(detail.getEvent_date())) 
                && now.isBefore(detail.getEventEndTime());
    }
    
}
