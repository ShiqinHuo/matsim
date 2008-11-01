/* *********************************************************************** *
 * project: org.matsim.*
 * TabularFileParser.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.utils.io.tabularFileParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Parser for plain text files that are structured in columns.
 *
 * @author gunnar
 */
public class TabularFileParser {

    private TabularFileParserConfig config = null;

    private boolean isStart(String line) {
        final String regex = this.config.getStartRegex();
        if (regex == null)
            return true;
        return line.matches(regex);
    }

    private boolean isEnd(String line) {
        final String regex = this.config.getEndRegex();
        if (regex == null)
            return false;
        return line.matches(regex);
    }

    private boolean isComment(String line) {
        final String regex = this.config.getCommentRegex();
        if (regex == null)
            return false;
        return line.matches(regex);
    }

    private String[] split(String line) {
        final String regex = this.config.getDelimiterRegex();
        if (regex == null)
            return new String[] { line };
        return line.split(regex);
    }

    /**
     * Parses the file specified in <code>config</code> as specified in
     * <code>config</code>. All relevant rows of the file are split into
     * columns and then are passed to the <code>handler</code>.
     *
     * @param config
     *            defines in what way the file is to be pared <em>formally</em>
     * @param handler
     *            defines in what way the parsed file is to be treated
     *            <em>logically</em>
     *
     * @throws NullPointerException
     *             if <code>config</code> is <code>null</code>
     * @throws NullPointerException
     *             if <code>handler</code> is <code>null</code>
     * @throws IOException
     */
    public void parse(TabularFileParserConfig config,
            TabularFileHandler handler) throws IOException {
        if (config == null)
            throw new NullPointerException(
                    "TabularFileParser requires a non-null configuration.");
        if (handler == null)
            throw new NullPointerException(
                    "TabularFileParser requires a non-null handler.");

        this.config = config;

        boolean started = (config.getStartRegex() == null);
        boolean ended = false;
        
        BufferedReader reader = new BufferedReader(new FileReader(config.getFile()));
        try {
        	String line;
	        while ((line = reader.readLine()) != null && !ended) {
	            if (started) {
	                ended = isEnd(line);
	                if (!ended && !isComment(line)) {
	                    handler.startRow(split(line));
	                }
	            } else {
	                started = isStart(line);
	            }
	        }
	      // no catch-block, the exception is passed upwards, but still try to close the reader
        } finally {
        	reader.close();
        }
    }

}