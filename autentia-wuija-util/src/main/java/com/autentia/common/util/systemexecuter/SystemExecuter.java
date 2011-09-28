/**
 * Copyright 2008 Autentia Real Business Solutions S.L.
 * 
 * This file is part of autentia-util.
 * 
 * autentia-util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * autentia-util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with autentia-util. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.common.util.systemexecuter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class implements an util class to execute external commands from java.
 * It needs an class that implements <b>ISystemExecuterParser</b> that this class
 * callbacks on std and err output of the executed command.
 *
 */
public class SystemExecuter {

	private static final Log log = LogFactory.getLog(SystemExecuter.class);
	private final ISystemExecuterParser stdParser;
	private final ISystemExecuterParser errParser;
	
	private boolean executeInBackground = true;
	private Integer delay = null;

	private Exception exception = null;
	
	/**
	 * Inner class that reads content of a stream and do a callback of 
	 * <b>ISystemExecuterParser</b> passed in constructor
	 */
	private class OutputReaderThread extends Thread {

		/* buffer length */
		private static final int BUFFER_LENGTH = 1024;
		/* input stream where read */
		private final InputStream is;
		/* parser with callback method*/
		private final ISystemExecuterParser parser;
		
		/**
		 * Constructor
		 * 
		 * @param is inputStream where read data
		 * @param parser parser to callback
		 */
		public OutputReaderThread (InputStream is, ISystemExecuterParser parser) {
			this.is = is;
			this.parser = parser;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			byte[] buffer = new byte[BUFFER_LENGTH];
			int bytesReaded = 1;
			byte[] totalBuffer = new byte[BUFFER_LENGTH * 3];
			int index = 0;
			try {
				while ((bytesReaded = is.read(buffer)) > 0) {
					for (int i = 0; i < bytesReaded; i++) {
						totalBuffer[index] = buffer[i];
						index++;
					}
					if (log.isTraceEnabled()) {
						final String s = new String(buffer);
						log.trace("Bytes readed: '" + s + "'");
					}
					Arrays.fill(buffer, (byte)0);
				}
				if (parser != null) {
					parser.parseCallBack(totalBuffer, index);
				}
			} catch (IOException e) {
				log.error(e);
				parser.parseCallBack(null, -1);
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Inner class that launch a command. Needed for delayed executions
	 */
	private class ExecuterThread extends Thread {

		private final String[] command;
					
		private Integer exitValue = null;
		
		/**
		 * Constructor
		 * 
		 * @param command
		 */
		public ExecuterThread(String[] command) {
			this.command = command;
		}

		Integer getExitValue() {
			return exitValue; 
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
		
			Process child = null;
			boolean hasDelay = (delay != null && delay.intValue() > 0);
			exception = null;
			
			try {
				if (log.isDebugEnabled()) {
					StringBuilder sb = new StringBuilder("executing command: " + Arrays.toString(command));
					if (executeInBackground) {
						sb.append(" with execution in background");
					}
					
					if (hasDelay) {
						if (executeInBackground) {
							sb.append(" and");
						}
						sb.append(" with delay: " + delay);
					}
					
					log.debug(sb.toString());
				}
				
				if (hasDelay) {
					sleep(delay.intValue() * 1000);
				}
				
				child = Runtime.getRuntime().exec(command);
								
				Thread stdThread = new OutputReaderThread(child.getInputStream(), stdParser);
				Thread errThread = new OutputReaderThread(child.getErrorStream(), errParser);
				
				stdThread.start();
				errThread.start();
				
				if (!executeInBackground) {
					child.waitFor();
				}
				
			} catch (IOException e) {
				log.error(e);
				exception = e;
			} catch (InterruptedException e) {
				log.error(e);
				exception = e;
			} 
		}
	}

	/**
	 * Constructor of class with no delay
	 * 
	 * @param stdParser parser of standard output
	 * @param errParser parser of error output
	 * @param executeInBackground true if execute command in background
	 */
	public SystemExecuter(ISystemExecuterParser stdParser, ISystemExecuterParser errParser, boolean executeInBackground, Integer delay) {
		this.stdParser = stdParser;
		this.errParser = errParser;
		this.executeInBackground = executeInBackground;
		this.delay = delay;
	}
	
	/**
	 * Constructor of class with no delay
	 * 
	 * @param stdParser parser of standard output
	 * @param errParser parser of error output
	 * @param executeInBackground true if execute command in background
	 */
	public SystemExecuter(ISystemExecuterParser stdParser, ISystemExecuterParser errParser, boolean executeInBackground) {
		this (stdParser, errParser, executeInBackground, null);
	}
	
	/**
	 * Constructor of class with execution in background and no delay
	 * 
	 * @param stdParser parser of standard output
	 * @param errParser parser of error output
	 */
	public SystemExecuter(ISystemExecuterParser stdParser, ISystemExecuterParser errParser) {
		this(stdParser, errParser, true);
	}
	
	
	/**
	 * Method that executes a system command
	 * 
	 * @param command command with parameters to execute
	 * @throws ExecutionException if an error ocurred during execution
	 */
	public void execute (String[] command) throws ExecutionException {
		
		ExecuterThread executerThread = new ExecuterThread(command);
		
		try {
			executerThread.start();
		
			if (!executeInBackground) {
				executerThread.join();
			}
			
			if (exception != null) {
				throw new ExecutionException(exception);
			}
		} catch (InterruptedException e) {
			log.error(e);
			throw new ExecutionException(e);
		}
	}
}
