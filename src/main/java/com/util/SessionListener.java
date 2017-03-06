package com.util;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;



public class SessionListener implements HttpSessionListener{

    private SessionContext context = SessionContext.getInstance();


    public void sessionCreated(HttpSessionEvent sessionEvent) {
    	sessionEvent.getSession().setMaxInactiveInterval(60 * 60 * 12);
        context.addSession(sessionEvent.getSession());
    }


    public void sessionDestroyed(HttpSessionEvent sessionEvent) {
        HttpSession session = sessionEvent.getSession();
        context.delSession(session);
    }

}
