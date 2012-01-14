/*
 * Copyright (C) 2012 Alex Cojocaru
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.alexalecu.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Alex Cojocaru
 *
 */
public class FileUtilTest {
	
	@Test
	public void testGenerateUniqueFilenameNonExistingFile() throws IOException {
		// file does not exist
		File file = generateUniqueFilename();
		String generated = FileUtil.generateUniqueFilename(file.getParent(), file.getName());
		Assert.assertEquals("Invalid unique filename generated", file.getName(), generated);
		FileUtil.deleteFileOrDirectory(file);
	}
	
	@Test
	public void testGenerateUniqueFilenameTwice() throws IOException {
		// generate 2 names and verify that they are equal
		File file = createUniqueFile();
		String generated = FileUtil.generateUniqueFilename(file.getParent(), file.getName(), 5);
		Assert.assertEquals("Invalid unique filename generated",
				file.getName() + "_00001", generated);
		
		String generatedAgain = FileUtil.generateUniqueFilename(file.getParent(), file.getName(), 5);
		Assert.assertEquals("Invalid unique filename generated again",
				file.getName() + "_00001", generatedAgain);

		FileUtil.deleteFileOrDirectory(file);
	}

	
	@Test
	public void testGenerateUniqueFilenameAgainAfterCreation() throws IOException {
		// create the first generated file, generate a new one
		File file = createUniqueFile();
		String generated = FileUtil.generateUniqueFilename(file.getParent(), file.getName(), 5);
		Assert.assertEquals("Invalid unique filename generated",
				file.getName() + "_00001", generated);
		
		File generatedFile = new File(file.getParent(), generated);
		generatedFile.createNewFile();
		
		generated = FileUtil.generateUniqueFilename(file.getParent(), file.getName(), 5);
		Assert.assertEquals("Invalid unique filename generated",
				file.getName() + "_00002", generated);
		
		FileUtil.deleteFileOrDirectory(generatedFile);
		FileUtil.deleteFileOrDirectory(file);
	}
	
	@Test
	public void testRemoveAutoGeneratedSuffix() {
		String filename = FileUtil.removeAutoGeneratedSuffix("test1 test2 test3.ext");
		Assert.assertEquals("Wrong auto generated suffix found", "test1 test2 test3.ext", filename);

		filename = FileUtil.removeAutoGeneratedSuffix("test1 test2 test3_12345.ext", 5);
		Assert.assertEquals("Wrong auto generated suffix found", "test1 test2 test3.ext", filename);

		filename = FileUtil.removeAutoGeneratedSuffix("test1 test2_.ext", 5);
		Assert.assertEquals("Wrong auto generated suffix found", "test1 test2_.ext", filename);
		
		filename = FileUtil.removeAutoGeneratedSuffix("test1 test2_12.ext", 1);
		Assert.assertEquals("Wrong auto generated suffix found", "test1 test2_12.ext", filename);
		
	}
	
	@Test
	public void testReplaceWhitespaces() {
		String filename = FileUtil.replaceWhitespaces("test1 test2 test3.ext");
		Assert.assertEquals("Wrong replacement", "test1_test2_test3.ext", filename);
		
		filename = FileUtil.replaceWhitespaces("test1test2_test3.ext");
		Assert.assertEquals("Wrong replacement", "test1test2_test3.ext", filename);
	}
	
	@Test
	public void testGetExtension() {
		String ext = FileUtil.getExtension("test.ext");
		Assert.assertEquals("Wrong file extension found", ".ext", ext);
		
		ext = FileUtil.getExtension("test.ext1.ext2");
		Assert.assertEquals("Wrong file extension found", ".ext2", ext);

		ext = FileUtil.getExtension("test");
		Assert.assertEquals("Wrong file extension found", "", ext);
	}
	
	@Test
	public void testStripExtension() {
		String basename = FileUtil.stripExtension("test.ext");
		Assert.assertEquals("Wrong file basename found", "test", basename);
		
		basename = FileUtil.stripExtension("test.ext.ext");
		Assert.assertEquals("Wrong file basename found", "test.ext", basename);
		
		basename = FileUtil.stripExtension("test");
		Assert.assertEquals("Wrong file basename found", "test", basename);
	}
	
	@Test
	public void testExists() throws IOException {
		File file = createUniqueFile();
		Assert.assertTrue("Existing file reported as non-existing",
				FileUtil.exists(file.getCanonicalPath()));
		FileUtil.deleteFileOrDirectory(file);

		file = generateUniqueFilename();
		Assert.assertFalse("Non-existing file reported as existing",
				FileUtil.exists(file.getCanonicalPath()));
	}

	@Test(expected=IOException.class)
	public void testWriteFileToNewNoOverwrite() throws IOException {
		File file = createUniqueFile();
		try {
			FileUtil.writeFile("test", file, false);
		}
		catch (IOException ex)
		{
			throw ex;
		}
		finally {
			FileUtil.deleteFileOrDirectory(file);
		}
	}

	@Test
	public void testWriteFileToNew() throws IOException {
		// write to existing file
		File file = createUniqueFile();
		writeRandomContent(file);
		FileUtil.writeFile("test", file, true);
		String content = readContent(file);
		FileUtil.deleteFileOrDirectory(file);
		Assert.assertEquals("File content does not match", "test", content);
		
		// write to non-existing file
		file = generateUniqueFilename();
		FileUtil.writeFile("test", file, false);
		content = readContent(file);
		FileUtil.deleteFileOrDirectory(file);
		Assert.assertEquals("File content does not match", "test", content);
	}
	
	@Test(expected=IOException.class)
	public void testCopyNonExistingFile() throws IOException {
		File src = generateUniqueFilename();
		File dest = generateUniqueFilename();
		FileUtil.copyFiles(src, dest, false);
	}
	
	@Test(expected=IOException.class)
	public void testCopyFileOverwrite() throws IOException {
		File src = createUniqueFile();
		File dest = createUniqueFile();
		try {
			FileUtil.copyFiles(src, dest, false);
		}
		catch (IOException ex)
		{
			throw ex;
		}
		finally {
			FileUtil.deleteFileOrDirectory(src);
			FileUtil.deleteFileOrDirectory(dest);
		}
	}
	
	@Test
	public void testCopyFileToNew() throws IOException {
		File src = createUniqueFile();
		String content = writeRandomContent(src);
		
		File dest = generateUniqueFilename();
		FileUtil.copyFiles(src, dest, false);
		String contentDest = readContent(dest);

		FileUtil.deleteFileOrDirectory(src);
		FileUtil.deleteFileOrDirectory(dest);
		
		Assert.assertEquals("File content don't match", content, contentDest);
	}
	
	@Test
	public void testCopyFileToExisting() throws IOException {
		File src = createUniqueFile();
		String content = writeRandomContent(src);
		
		File dest = createUniqueFile();
		writeRandomContent(dest);
		FileUtil.copyFiles(src, dest, true);
		String contentDest = readContent(dest);

		FileUtil.deleteFileOrDirectory(src);
		FileUtil.deleteFileOrDirectory(dest);
		
		Assert.assertEquals("File content don't match", content, contentDest);
	}
	
	@Test
	public void testDeleteFile() throws IOException {
		File uniqueFile = generateUniqueFilename();
		
		boolean result = FileUtil.deleteFileOrDirectory(uniqueFile);
		Assert.assertTrue("Failed trying to delete non-existing directory", result);
		
		uniqueFile = createUniqueFile();

		result = FileUtil.deleteFileOrDirectory(uniqueFile);
		Assert.assertTrue("Failed trying to delete existing file", result);
		Assert.assertFalse("Surprise! File was not delete as expected", uniqueFile.exists());
	}
	
	@Test
	public void testDeleteDirectory() throws IOException {		
		File uniqueDir = createUniqueDirectory();
		createUniqueDirectory(uniqueDir);
		File unique = createUniqueDirectory(uniqueDir);
		createUniqueFile(unique);
		
		boolean result = FileUtil.deleteFileOrDirectory(uniqueDir);
		Assert.assertTrue("Failed trying to recursively delete existing directory", result);
		Assert.assertFalse("Surprise! Directory was not delete as expected", uniqueDir.exists());
	}

	@Test
	public void testHumanReadableFileSize() {
		Assert.assertEquals("1.43 Kb", FileUtil.getHumanReadableFileSize(1460));
		Assert.assertEquals("10 b", FileUtil.getHumanReadableFileSize(10));
		Assert.assertEquals("1.36 Gb", FileUtil.getHumanReadableFileSize(1460432432));
	}

	/**
	 * create unique file in the working directory
	 * @return the newly created file
	 * @throws IOException
	 */
	public static File createUniqueFile() throws IOException {
		return createUniqueFile(new File("."));
	}
	
	/**
	 * create unique file in the given directory
	 * @param parentDir
	 * @return the newly created file
	 * @throws IOException
	 */
	public static File createUniqueFile(File parentDir) throws IOException {
		File unique = generateUniqueFilename(parentDir);
		if (!unique.createNewFile())
			throw new IOException("Cannot create unique file for testing - "
					+ unique.getCanonicalPath());
		return unique;
	}

	/**
	 * create unique directory in the working directory
	 * @return the newly created directory
	 * @throws IOException
	 */
	public static File createUniqueDirectory() throws IOException {
		return createUniqueDirectory(new File("."));
	}
	
	/**
	 * create unique directory in the given directory
	 * @param parentDir
	 * @return the newly created directory
	 * @throws IOException
	 */
	public static File createUniqueDirectory(File parentDir) throws IOException {
		File unique = generateUniqueFilename(parentDir);
		if (!unique.mkdir())
			throw new IOException("Cannot create unique directory for testing - "
					+ unique.getCanonicalPath());
		return unique;
	}
	
	/**
	 * @return an unique file (not created yet) in the current directory
	 * @throws an IOException if an unique name cannot be found after 10 tries
	 */
	private File generateUniqueFilename() throws IOException {
		return generateUniqueFilename(new File("."));
	}
	
	/**
	 * @param parentDir the directory where to look for unique file names
	 * @return an unique file (not created yet) in the given directory
	 * @throws an IOException if an unique name cannot be found after 10 tries
	 */
	public static File generateUniqueFilename(File parentDir) throws IOException {
		int tries = 10;
		while (tries-- > 0)
		{
			File uniqueDir = new File(parentDir, UUID.randomUUID().toString());
			if (!uniqueDir.exists())
				return uniqueDir;
		}
		
		throw new IOException("Cannot generate unique filename after trying 10 times");
	}
	
	/**
	 * Read the content from the given file
	 * @param file
	 * @return the file content
	 * @throws IOException
	 */
	public static String readContent(File file) throws IOException {
		if (!file.exists())
			throw new IOException("Source file does not exist");

		FileInputStream stream = new FileInputStream(file);
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			return Charset.defaultCharset().decode(bb).toString();
		}
		finally {
			stream.close();
		}
	}
	
	/**
	 * Write some random text into the given file
	 * @param file
	 * @return the content written
	 * @throws IOException
	 */
	public static String writeRandomContent(File file) throws IOException {
		if (!file.exists())
			throw new IOException("Target file does not exist");
		
		String newLine = System.getProperty("line.separator");
		
		// create some random content
		StringBuilder content = new StringBuilder();
		for (int i = 0; i < 3; i++) {
			content.append(UUID.randomUUID().toString()).append(newLine);
		}
		
		// and save it into the file
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			bw.append(content);
		}
		finally {
			if (bw != null) bw.close();
		}
		
		return content.toString();
	}
}
