/**
 * 
 * 产生2gram概率矩阵
 * 
 */

package gram;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Gram {
	String inFilename_ = null;
	String proFilename_ = "probability.txt";
	String wordFilename_ = "word.txt";
	double[][] probability_;
	ArrayList<Character> words_ = null;
	ArrayList<Integer> wordCount_ = null;

	String content_ = new String();

	Gram(String filename) {
		this.inFilename_ = filename;
		ReadIn readObj = new ReadIn(inFilename_);
		readObj.read();
		// //
		System.out.println("read in finished");
		// //
		content_ = readObj.getContent_().toString();
		words_ = readObj.getWords_();
		wordCount_ = readObj.getWordCount_();
		int size = readObj.getSize_();
		probability_ = new double[size][size];
	}

	public void outputWords() {
		FileOutputStream wordFileout = null;
		try {
			wordFileout = new FileOutputStream(wordFilename_);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					wordFileout));
			for (int i = 0; i < words_.size(); i++) {
				bw.write(words_.get(i) + "|");
				/**
				 * TODO understand
				 */
				bw.write(String.valueOf(wordCount_.get(i)));
				bw.write("\n");
			}
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				wordFileout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param progress
	 *            progress percentage(e.g. 99.3%)
	 * @param elapsedTime
	 *            unit: millisecond
	 */
	private void showProgress_(double progress, long elapsedTime, int line) {
		long leftTime = (long) ((double) elapsedTime / progress) - elapsedTime;
		int hourE = (int) (elapsedTime / 1000 / 60 / 60);
		int minE = (int) (elapsedTime / 1000 / 60 - hourE * 60);
		int secE = (int) (elapsedTime / 1000 - hourE * 60 * 60 - minE * 60);
		int hourR = (int) (leftTime / 1000 / 60 / 60);
		int minR = (int) (leftTime / 1000 / 60 - hourR * 60);
		int secR = (int) (leftTime / 1000 - hourR * 60 * 60 - minR * 60);
		System.out.println(String.format("Elapsed Time: %d h %d m %d s", hourE,
				minE, secE));
		System.out.println(String.format("Current line: %d", line));
		System.out.println(String.format("Process: %2.5f%%", progress * 100));
		System.out.println(String.format("Remaining Time:%d h %d m %d s",
				hourR, minR, secR));
	}

	/**
	 * Store the appearance of line [begin, end), Then clean the appearance
	 * matrix
	 * 
	 * @param appearance
	 * @param begin
	 * @param end
	 */
	private void storeAppearanceCleaned(int[][] appearance, int begin, int end) {
		FileOutputStream PrFileOut = null;
		String filename = String.format("P_%d_%d", begin, end);
		try {
			PrFileOut = new FileOutputStream(filename);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					PrFileOut));
			for (int i = 0; i < appearance.length; i++) {
				for (int j = 0; j < appearance[0].length; j++) {
					bw.write(String.format("%d ", appearance[i][j]));
				}
				bw.write("\n");
			}
			// clean the matrix
			int len = appearance.length;
			for (int i = 0; i < len; i++)
				for (int j = 0; j < len; j++)
					appearance[i][j] = 0;
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				PrFileOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private int[][] matrixPlus_(int[][] m, int[][] n) {
		if (m.length != n.length || m[0].length != n[0].length) {
			System.out.println("error at Gram::matrixPlus. Size not matched");
			return null;
		}
		int[][] result = new int[m.length][m[0].length];
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[0].length; j++) {
				result[i][j] = m[i][j] + n[i][j];
			}
		}
		//TODO problems?
		return result;
	}

	
	/**
	 * [begin,end)
	 * 
	 * @param begin
	 * @param end
	 * @param granularity
	 */
	private void mergeAppearanceFiles(int begin, int end, int granularity) {
		int[][] sumAppearance = new int[words_.size()][words_.size()];
		for (int i = begin; i < end; i += granularity) {
			System.out.println(String.format("Merging line:%d", i));
			int[][] tmpAppearance = new int[words_.size()][words_.size()];
			int beginLine = i;
			int endLine = i + granularity;
			if (beginLine + granularity > end)
				endLine = end;
			String filename = String.format("P_%d_%d", beginLine, endLine);
			FileInputStream filein = null;
			try {
				filein = new FileInputStream(filename);
				BufferedReader br = new BufferedReader(new InputStreamReader(
						filein));
				String line = br.readLine();
				int lineCount = 0;
				while (line != null) {
					//TODO regex
					//String[] str = line.split("\\s+");
					String[] str = line.split(" ");
					for (int colCount = 0; colCount < str.length; colCount++) {
						tmpAppearance[lineCount][colCount] = Integer.valueOf(str[colCount]);
					}
					//System.out.println(String.format("line:%d", lineCount));
					line = br.readLine();
					lineCount++;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//PLUS
			sumAppearance = matrixPlus_(sumAppearance, tmpAppearance);
		}
		
		//OUTPUT
		String outFilename = String.format("P_%d_%d", begin, end);
		FileOutputStream proFileout = null;
		try {
			proFileout = new FileOutputStream(outFilename);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					proFileout));
			for (int i = 0; i < words_.size(); i++) {
				for (int j = 0; j < words_.size(); j++) {
					bw.write(String.format("%d ", sumAppearance[i][j]));
				}
				bw.write("\n");
			}
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				proFileout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// TODO better than computePro2
	/**
	 * Compute probability of phrases from line start to line end, i.e. [start,
	 * end). Store the result into a file with a granularity
	 * 
	 * @param begin
	 * @param end
	 */
	public void computePartPr(int begin, int end, int granularity) {
		int wordSize = words_.size();
		int[][] appearance = new int[wordSize][wordSize];
		String[] lines = content_.split("\n");
		if (end > lines.length)
			end = lines.length;

		// for efficiency reasons
		Object[] wordArrayTmp = words_.toArray();
		char[] wordArray = new char[wordSize];
		for (int i = 0; i < wordSize; i++) {
			wordArray[i] = (char) wordArrayTmp[i];
		}

		// for progress display issue
		Date startTime = new Date();
		int wordCount = 0;
		int partWordCount = 0;
		for (int i = begin; i < end; i++) {
			partWordCount += lines[i].length();
		}

		// for(String line : lines){
		for (int l_count = begin; l_count < end; l_count++) {
			System.out.println("Current line is: " + l_count);
			char[] chars = lines[l_count].toCharArray();
			for (int c_count = 0; c_count < chars.length - 1; c_count++) {
				wordCount++;
				//skip none Chinese symbols
				boolean isLastNoneChinese = false;
				while(!String.valueOf(chars[c_count]).matches("[\\u4e00-\\u9fff]+")
						|| !String.valueOf(chars[c_count + 1]).matches("[\\u4e00-\\u9fff]+")) {
					wordCount++;
					c_count++;
					if(c_count >= chars.length - 1){
						isLastNoneChinese = true;
						break;
					}
						
				}
				if(isLastNoneChinese)
					continue;
				String target = String.valueOf(chars[c_count])
						+ chars[c_count + 1];
				System.out.println("target: " + target);
				int i = words_.indexOf(chars[c_count]);
				int j = words_.indexOf(chars[c_count + 1]);
				if(i >= 0 && j >= 0)
					appearance[i][j]++;
				Date nowTime = new Date();
				double progress = (double) wordCount / partWordCount;
				showProgress_(progress, nowTime.getTime() - startTime.getTime(), l_count);
			}
			if (l_count + 1 == end) {
				this.storeAppearanceCleaned(appearance, l_count / granularity * granularity, l_count + 1);
				continue;
			}
			if ((l_count - begin + 1) % granularity == 0)
				this.storeAppearanceCleaned(appearance, l_count - granularity
						+ 1, l_count + 1);
		}

	}

	public static void main(String args[]) {
//		Gram g = new Gram("Corpus/my_training");
		//Gram g = new Gram("Corpus/pku_test_utf8");
		Gram g = new Gram("Corpus/pku_training_utf8");

		g.outputWords();
		g.computePartPr(0, 19000, 19000);
//		g.mergeAppearanceFiles(920, 1000, 20);
		// g.computePr();
		// g.computePro2();
	}
}
