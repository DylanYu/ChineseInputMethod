package inputmethod;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

import segment.Segment;

public class InputMethod {
	String spelling_ = null;
	String outputWords_ = null;
	int[][] phraseCount_ = null;
	Hashtable<String, String> dict_ = null;
	
	//所有字的列表
	ArrayList<Character> words_ = null;
	//所有字的出现次数，对应于列表
	ArrayList<Integer>wordCount_ = null;
	
	/**
	 * 拼音部分
	 */
	//所有词(两个字)
	ArrayList<String> phrases_ = new ArrayList<String>();
	//所有词对应的拼音，格式"hao de"
	ArrayList<String> phraseSpelling_ = new ArrayList<String>();
	
//	String wordFilename_ = "word.txt";
//	String proFilename_ = "P_0_1900";
	String wordFilename_ = "newword.txt";
	String proFilename_ = "P_0_19000";
	String spellingFilename_ = "DoubleSpelling.txt";
	String dictFilename_ = "dict";
	
	InputMethod(){
		words_ = new ArrayList<Character>();
		//outputWords_= new Hashtable<String, Double>();
		dict_ = new Hashtable<String, String>();
		wordCount_ = new ArrayList<Integer>();
//		wordPr_ = new ArrayList<Double>();
	}
	
	private void readDictFile() {
		DataInputStream dictFilein = null;
		try {
			dictFilein = new DataInputStream(new FileInputStream(dictFilename_));
			BufferedReader br = new BufferedReader(new InputStreamReader(dictFilein));
			String line = br.readLine();
			while(line != null) {
				String temp[] = line.split("\t");
				String index = temp[0];
				String values = temp[1];
				dict_.put(index, values);
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readWordFile() {
		FileInputStream wordFilein = null;
		try {
			wordFilein = new FileInputStream(wordFilename_);
			BufferedReader br = new BufferedReader(new InputStreamReader(wordFilein));
			String line = br.readLine();
			while(line != null) {
				String[] strs = line.split("\\|");
				words_.add(strs[0].toCharArray()[0]);
				//CAUTION
				wordCount_.add(Integer.parseInt(strs[1]));
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//init phraseCount_ with size
		phraseCount_ = new int[words_.size()][words_.size()];
	}
	
	private void readPhraseCountFile() {
		FileInputStream proFilein = null;
		try {
			proFilein = new FileInputStream(proFilename_);
			BufferedReader br = new BufferedReader(new InputStreamReader(proFilein));
			String line = br.readLine();
			int i = 0;
			while(line != null) {
				String[] strs = line.split(" ");
				for(int j = 0; j < strs.length; j++){
					phraseCount_[i][j] = Integer.valueOf(strs[j]);
				}
				i++;
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readSpellingFile() {
		FileInputStream spellingFilein = null;
		try {
			spellingFilein = new FileInputStream(spellingFilename_);
			BufferedReader br = new BufferedReader(new InputStreamReader(spellingFilein));
			String line = br.readLine();
			while(line != null) {
				String[] strs = line.split("\\t");
				phrases_.add(strs[0]);
				String[] py = strs[1].split("\\|");
				phraseSpelling_.add(py[0] + " " + py[1]);
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readFile() {
		//sequence can't be changed
		this.readWordFile();
		this.readPhraseCountFile();
		this.readDictFile();
		this.readSpellingFile();
	}
	
	/**
	 * 
	 * @param spelling
	 * 		spelling for s a single word,suck as 'hao', 'de'.
	 * @return
	 * 		waiting words for the spelling.
	 * 		e.g.'康慷糠扛抗亢炕伉闶钪' for 'kang'
	 */
	private String findWaitingWords(String spelling) {
		String waitingWords = dict_.get(spelling);
		return waitingWords;
	}
	
	public String segment(String spelling){
		Segment segment=new Segment();
	    String result = segment.getFullSeg(spelling);
	    return result;
	}
	
	/**
	 * 从候选字里选出语料中出现次数最多的返回
	 * @param words1
	 * @param words2
	 * @return
	 */
	private String getDefault(char[] words1, char[] words2) {
		char word1 = ' ';
		char word2 = ' ';
		System.out.println("choose default");
		int maxCount = 0;
		char maxChar = ' ';
		boolean hasChanegd2 = false;
		for(int i = 0; i < words1.length; i++) {
			int index = words_.indexOf(words1[i]);
			if(index == -1)
				continue;
			int count = wordCount_.get(index);
			if(count > maxCount) {
				hasChanegd2 = true;
				maxCount = count;
				maxChar = words1[i];
			}
		}
		if(!hasChanegd2) {
			/**
			 * 即在选择默认字时，该读音内候选字都没有在语料库中出现。这是语料库太小所致。
			 */
			System.out.println("error in InputMethod:getDefault1:hasChanegd2");
			maxChar = words1[0];
		}
		word1 = maxChar;
		maxCount = 0;
		maxChar = ' ';
		hasChanegd2 = false;
		for(int i = 0; i < words2.length; i++) {
			int index = words_.indexOf(words2[i]);
			if(index == -1)
				continue;
			int count = wordCount_.get(index);
			if(count > maxCount) {
				hasChanegd2 = true;
				maxCount = count;
				maxChar = words2[i];
			}
		}
		if(!hasChanegd2) {
			/**
			 * 即在选择默认字时，该读音内候选字都没有在语料库中出现。这是语料库太小所致。
			 */
			System.out.println("error in InputMethod:getDefault2:hasChanegd2");
			maxChar = words2[0];
		}
		word2 = maxChar;
		StringBuffer sb = new StringBuffer();
		sb.append(word1);
		sb.append(word2);
		return sb.toString();
	}
	
	/**
	 * 获得词在训练的gram中的出现次数
	 * @param phrase
	 * 		两个字的词
	 * @return
	 */
	private int getPhraseCount(String phrase) {
		char[] words = phrase.toCharArray();
		if(words.length != 2) {
			System.out.println("Error in getCount: phrase length!=2");
			return -1;
		}
		char word1 = words[0];
		char word2 = words[1];
		int index1 = words_.indexOf(word1);
		int index2 = words_.indexOf(word2);
		if(index1 >0 && index2 > 0) {
			return phraseCount_[index1][index2];
		}
		//
		System.out.println("Maybe not good: phrase not found");
		return -1;
	}
	
	/**
	 * 根据拼音（双音节）如"hao de", "bu xing"，利用2-gram从训练集中得到词
	 * @param s0
	 * 		单音节，如"hao", "bu"
	 * @param s1
	 * 		单音节，如"de", "xing", "cfa"
	 * @return
	 */
	private String biGram(String s0, String s1) {
		char[] words1 = findWaitingWords(s0).toCharArray();
		char[] words2 = findWaitingWords(s1).toCharArray();
		char word1 = ' ';
		char word2 = ' ';
		if(words1.length < 1 || words2.length < 1) {
			System.out.println("spelling Error: biGram()");
			return null;
		}

		int max_i = 0;
		int max_j = 0;
		double maxCount = 0;
		boolean hasChanged= false;
		for(int i = 0; i < words1.length; i++) {
			for(int j = 0; j < words2.length; j++) {
				int row = words_.indexOf(words1[i]);
				int col = words_.indexOf(words2[j]);
				if(row < 0 || col < 0)
					continue;
				int count = phraseCount_[row][col];
				if(count > maxCount) {
					hasChanged = true;
					maxCount = count;
					max_i = i;
					max_j = j;
				}
			}
		}
		if(hasChanged) {
			word1 = words1[max_i];
			word2 = words2[max_j];
		}
		//no matched phrase, so we should choose default word for every spelling, 
		//such as '不' for 'bu', '好' for 'hao', which is studied from training data
		else {
			char[] defaultResult = getDefault(words1, words2).toCharArray();
			word1 = defaultResult[0];
			word2 = defaultResult[1];
		}
		return new String(String.valueOf(word1) + word2);
	}
	
	/**
	 * 根据第一个字和第二个音，得到词
	 * @param word
	 * 		字
	 * @param spelling
	 * 		拼音
	 * @return
	 */
	private String biGramUnderWord(char word, String spelling) {
		char[] words1 = new char[1];
		words1[0] = word;
		char[] words2 = findWaitingWords(spelling).toCharArray();
		char word1 = ' ';
		char word2 = ' ';
		if(words1.length < 1 || words2.length < 1) {
			System.out.println("spelling Error: biGram()");
			return null;
		}

		int max_i = 0;
		int max_j = 0;
		double maxCount = 0;
		boolean hasChanged= false;
		for(int i = 0; i < words1.length; i++) {
			for(int j = 0; j < words2.length; j++) {
				int row = words_.indexOf(words1[i]);
				int col = words_.indexOf(words2[j]);
				if(row < 0 || col < 0)
					continue;
				int count = phraseCount_[row][col];
				if(count > maxCount) {
					hasChanged = true;
					maxCount = count;
					max_i = i;
					max_j = j;
				}
			}
		}
		if(hasChanged) {
			word1 = words1[max_i];
			word2 = words2[max_j];
		}
		//no matched phrase, so we should choose default word for every spelling, 
		//such as '不' for 'bu', '好' for 'hao', which is studied from training data
		else {
			char[] defaultResult = getDefault(words1, words2).toCharArray();
			word1 = defaultResult[0];
			word2 = defaultResult[1];
		}
		return new String(String.valueOf(word1) + word2);
	}
	
	/**
	 * 从候选phrase中找到count最大的返回
	 * @param waitingIndex
	 * @return
	 */
	private String selectPhrase(ArrayList<Integer> waitingIndex){
		ArrayList<String> waitingPhrase = new ArrayList<String>();
		for(int index: waitingIndex) {
			waitingPhrase.add(phrases_.get(index));
		}
		int maxPhraseCount = 0;
		String maxPhrase = "";
		boolean isChanged = false;
		for(String phrase: waitingPhrase) {
			int count = getPhraseCount(phrase);
			if(count > maxPhraseCount) {
				isChanged = true;
				maxPhraseCount = count;
				maxPhrase = phrase;
			}
		}
		if(!isChanged) {
			System.out.println("display error in selectPhrase");
			for(String phrase: waitingPhrase){
				System.out.println(phrase);
			}
			//这几个匹配读音的词都没法在训练集中找到（应该不太会出现这种情况），直接把第一个结果返回
			System.out.println("在拼音集中存在的词竟然在训练集中找不到！");
			maxPhrase = waitingPhrase.get(0);
		}
		return maxPhrase;
	}
	
	/**
	 * 
	 * @param spelling
	 * 		spelling for multi-words, suck as 'haode', 'meiyou', 'wodegushi'
	 */
	public void computeWords(String spelling) {
		String[] segmentedSpelling = segment(spelling).split(" ");
		StringBuffer buffer = new StringBuffer();
		Hashtable<Double, String> candidates = new Hashtable<Double, String>();
		ArrayList<String> ws = new ArrayList<String>();
		ArrayList<Double> pros = new ArrayList<Double>();
		char last = ' ';
		
		String[] seg = segmentedSpelling;
		
		for(int k = 0; k < segmentedSpelling.length - 1 /*-1 for 2gram*/; k++) {
			char word1 = ' ';
			char word2 = ' ';
			/**
			 * 先从拼音词组中选择
			 */
			String curSpelling = new String(seg[k] + " " + seg[k + 1]);
			//遍历phraseSpelling_， 查看有多少与该拼音匹配的词组
			ArrayList<Integer> spellingIndex = new ArrayList<Integer>();
			for(int index = 0; index < phraseSpelling_.size(); index++) {
				if(phraseSpelling_.get(index).equals(curSpelling)) {
					spellingIndex.add(index);
				}
			}
			int numOfMatch = spellingIndex.size();
			String selectedPhrase = "";
			
			//句子开头
			if(k == 0) {
				if(numOfMatch == 1) {
					selectedPhrase = phrases_.get(phraseSpelling_.indexOf(curSpelling));
				}
				else if(numOfMatch > 1) {
					//e.g.buxing
					selectedPhrase = selectPhrase(spellingIndex);
				}
				//没有拼音匹配，2gram m*n
				else {
					selectedPhrase = biGram(seg[k], seg[k + 1]);
				}
			}
			
			//非句子开头
			else {
				ArrayList<Integer> firstMatchIndex = new ArrayList<Integer>();
				for(int index: spellingIndex) {
					char firstWord = phrases_.get(index).toCharArray()[0];
					if(firstWord == last) {
						firstMatchIndex.add(index);
					}
				}
				int firstMatchCount = firstMatchIndex.size();
				if(numOfMatch == 0 || firstMatchCount == 0) {
					selectedPhrase = biGramUnderWord(last, seg[k + 1]);
				}
				else if(firstMatchCount > 0 && numOfMatch == 1) {
					selectedPhrase = phrases_.get(spellingIndex.get(0));
				}
				else if(firstMatchCount > 0 && numOfMatch > 1) {
					selectedPhrase = selectPhrase(firstMatchIndex);
				}
				else {
					System.out.println("不会吧");
				}
			}
			
			//FINAL PROCESS
			char[] chs = selectedPhrase.toCharArray();
			word1 = chs[0];
			word2 = chs[1];
			last = word2;
			if(k == 0) {
				buffer.append(word1);
				buffer.append(word2);
			}
			else {
				buffer.append(word2);
			}
		}

		this.outputWords_ = buffer.toString();
		System.out.println(outputWords_);
	}
	
	
	public static void main(String args[]) {
		InputMethod method = new InputMethod();
		method.readFile();
		method.computeWords("womenshidaxuesheng");
	}
}
