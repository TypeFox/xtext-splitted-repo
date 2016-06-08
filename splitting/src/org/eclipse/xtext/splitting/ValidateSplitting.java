/*******************************************************************************
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.splitting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateSplitting {
	
	public static final Set<String> REPOSITORIES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
		"core", "extras", "lib", "xtend", "eclipse", "idea", "web", "maven", "xtext-website", "xtend-website" 
	)));
	
	public static final String DELETE = "delete";
	
	public static void main(String[] args) {
		if (args.length != 2) {
			fail("Expected paths to splitting.txt and working directory as arguments.");
		}
		String workingDir = args[1];
		try {
			
			// Validate repositories and gather all paths from the splitting file
			final Set<String> specifiedPaths = new HashSet<>();
			try (BufferedReader reader = new BufferedReader(new FileReader(args[0]))) {
				String line;
				while ((line = reader.readLine()) != null) {
					if (!line.isEmpty()) {
						String[] parts = line.split(">>");
						if (parts.length != 2) {
							fail("Invalid line: " + line);
						}
						String[] repos = parts[1].split(",");
						if (repos.length == 0) {
							fail("Invalid line: " + line);
						}
						for (String repo : repos) {
							String trimmed = repo.trim();
							if (!(REPOSITORIES.contains(trimmed) || DELETE.equals(trimmed))) {
								fail("Invalid repository: " + trimmed);
							}
						}
						String path = parts[0].trim();
						specifiedPaths.add(path);
					}
				}
			}
			
			// Check whether each file has a specified path as prefix
			final Pattern segmentPattern = Pattern.compile("/");
			try (BufferedReader reader = new BufferedReader(new FileReader(workingDir + "/" + FindProjects.ALL_FILES))) {
				String line;
				while ((line = reader.readLine()) != null) {
					if (!line.isEmpty()) {
						String file = line.replaceAll("\"|\\\\.", "");
						if (!specifiedPaths.contains(file)) {
							Matcher matcher = segmentPattern.matcher(file);
							boolean foundSplitting = false;
							int lastMatch = -1;
							while (!foundSplitting && matcher.find()) {
								lastMatch = matcher.start();
								if (specifiedPaths.contains(file.substring(0, lastMatch)))
									foundSplitting = true;
							}
							if (!foundSplitting) {
								fail("File not covered by splitting: " + file);
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static void fail(String message) {
		System.err.print("ERROR: ");
		System.err.println(message);
		System.exit(1);
	}

}
