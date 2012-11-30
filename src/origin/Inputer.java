package origin;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class Inputer implements Def{
	private StringBuffer inputStr_ = null;
	private int state_ = 0;
	private Hashtable<String, String> dict_ = null;
	private File file_ = null;
	private static volatile Inputer INSTANCE_ = null;
	
	private String output_ = null;
	private ArrayList<String> selectiveList_ = null;
	private final int bufferSize = 30000;
	
	//maybe there's a better solution?
	private int inputStrLenBeforeINITATE_ = 0;
	
	//enter to input English
	private boolean isEnglish_;

	/**
	 * process user's input, then produce the expected output and a selective list
	 * @param ch
	 */
	public void process(char ch) {
		if(state_ == INITATE) {
			//inputStr_.delete(0, inputStr_.length());
			inputStrLenBeforeINITATE_ = 0;
		}
		//letter
		if(Character.isLetter(ch)) {
			inputStr_.append(ch);
			inputStrLenBeforeINITATE_++;
			String foundListStr = dict_.get(inputStr_.toString());
			if(foundListStr != null){
				String listItem[] = foundListStr.trim().split("");
				for(int i = 0; i < listItem.length; i++)
					selectiveList_.add(listItem[i]);
			}
			else
				selectiveList_.clear();
			output_ = "";
			state_ = LEGAL_INPUT;
		}
		//digit
		else if(Character.isDigit(ch)) {
			//output_ = "digit" + String.valueOf(ch);
			if(state_ != LEGAL_INPUT) {
				output_ = String.valueOf(ch);
			}
			else {
				int selectedNum = Integer.valueOf(ch - '0');
				if(selectedNum > selectiveList_.size()){
					//error
					System.out.println("select index out of bound!");
					inputStr_.delete(0, inputStr_.length());
					state_ = INITATE;
					return;
				}
				else{
					output_ = selectiveList_.get(selectedNum);
					selectiveList_.clear();
				}
			}
			inputStr_.delete(0, inputStr_.length());
			state_ = INITATE;
		}
		//delete
		else if(ch == 8) {
			//if the state is INITATE, leave it
			if(state_ != INITATE && inputStr_.length() > 0){
				inputStr_.delete(inputStr_.length() - 1, inputStr_.length());
				inputStrLenBeforeINITATE_--;
				String foundListStr = dict_.get(inputStr_.toString());
				if(foundListStr != null){
					String listItem[] = foundListStr.trim().split("");
					for(int i = 0; i < listItem.length; i++)
						selectiveList_.add(listItem[i]);
				}
				else
					selectiveList_.clear();
				output_ = "";
				state_ = LEGAL_INPUT;
			}
		}
		/**
		 * the ENTER in windows represents two ascii code: 10 and 13.
		 * In practice, 10 works.
		 */
		else if (ch == 10) {
			output_ = inputStr_.toString();
			inputStr_.delete(0, inputStr_.length());
			selectiveList_.clear();
			state_ = INITATE;
			isEnglish_ = true;
		}
		//other code
		else {
			output_ = String.valueOf(ch);
			inputStr_.delete(0, inputStr_.length());
			state_ = INITATE;
		}
	}
	
	/**
	 * read the dict file and make a hash table for all index
	 */
	public void readDict() {
		DataInputStream in = null;
		try {
			in = new DataInputStream(new FileInputStream(file_));
			byte buffer[] = new byte[bufferSize];
			in.read(buffer, 0, bufferSize);
			String rawStr = new String(buffer).trim();
			String splitStr[] = rawStr.split("\n");
			for (int i = 0; i < splitStr.length; i++) {
				String temp[] = splitStr[i].split("\t");
				String index = temp[0];
				String values = temp[1];
				this.dict_.put(index, values);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Inputer() {
		inputStr_ = new StringBuffer();
		state_ = INITATE;
		isEnglish_ = false;
		dict_ = new Hashtable<String, String>();
		selectiveList_ = new ArrayList<String>();
		file_ = new File("Corpus/dict");
		readDict();
	}

	public static Inputer getInputer() {
		if (INSTANCE_ == null) {
			synchronized (Inputer.class) {
				if (INSTANCE_ == null) {
					INSTANCE_ = new Inputer();
					return INSTANCE_;
				}
			}
		}
		return INSTANCE_;
	}

	public void setInputStr_(StringBuffer str) {
		this.inputStr_ = str;
	}

	public StringBuffer getInputStr_() {
		return inputStr_;
	}

	public int getState_() {
		return state_;
	}

	public void setState_(int state) {
		this.state_ = state;
	}

	public String getOutput_() {
		return output_;
	}

	public  String getSelectiveListInfo() {
		StringBuffer info = new StringBuffer();
		/**
		 * start from 1, cuz position 0 is null
		 */
		for(int i = 1; i < selectiveList_.size(); i++){
			info.append(i + "." );
			info.append(selectiveList_.get(i));
			info.append(" ");
		}
		return info.toString();
	}
	
	/**
	 * 
	 * @return
	 * the length of inputStr plus 1, which is the digit we used to select a word
	 */
	public int getDeleteLen() {
		return inputStrLenBeforeINITATE_ + 1;
	}
	
	public boolean isEnglish() {
		return isEnglish_;
	}
	
	public void setIsEnglishFlase() {
		isEnglish_ = false;
	}

}
