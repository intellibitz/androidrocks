package com.retailwave.fce.client.content.i18n;
/**
 * $Id: SearchUserConstants.java 5 2010-06-03 11:07:35Z muthu $
 * $HeadURL: svn://10.10.200.111:3691/Finance/tags/framework-snapshot1/fce/src/main/java/com/retailwave/fce/client/content/i18n/SearchUserConstants.java $
 */

import com.google.gwt.i18n.client.Constants;

public interface SearchUserConstants extends Constants {
    String idPrefix();

    String title();

    String description();

    String shortName();

    String fullName();

    String email();

    String active();

    String role();

    String program();

    String country();

    String type();

    String[] typeValues();

    String[] activeValues();

    String[] actions();
}