/**
 * Author:	zhangxiang45245
 * http://blog.sina.com.cn/s/blog_5a5d07e60100n7lw.html
 */
package segment;

import java.io.IOException;
import java.util.ArrayList;
public class Segment {
   
    String result="";//最后要显示的结果
    private ArrayList<String> sm = new ArrayList<String>();//声母集合
    private ArrayList<String> ym = new ArrayList<String>();//韵母集合
    public void findsm(String py){
        sm.add("b");
        sm.add("c");
        sm.add("d");
        sm.add("f");
        sm.add("g");
        sm.add("h");
        sm.add("j");
        sm.add("k");
        sm.add("l");
        sm.add("m");
        sm.add("n");
        sm.add("p");
        sm.add("q");
        sm.add("r");
        sm.add("s");
        sm.add("t");
        sm.add("w");
        sm.add("x");
        sm.add("y");
        sm.add("z");
        sm.add("sh");
        sm.add("zh");
        sm.add("ch");
        char[] py2 = py.toCharArray();//将读入字符转为char数组
        int temp=0;
        //遍历声母集合，匹对
        for(int i=0;i<sm.size();i++){
            for(int j=1;j<=py2.length;j++){
                String py3 = String.copyValueOf(py2, 0, j);//截取从0开始到j结束的字符串
                if(py3.equals(sm.get(i))){
                    temp = sm.get(i).length();//对应的声母的长度
                }
            }
        }
        if(temp!=0){
            result = result+String.copyValueOf(py2,0,temp);//将匹对出来的声母赋给result
            py = py.substring(temp);
        }else{
            String end = String.valueOf(result.charAt(result.length()-2));
            for(int i=0;i<sm.size();i++){
                if(sm.get(i).equals(end)){
                    py = result.charAt(result.length()-2)+py;
                    result = result.substring(0, result.length()-2)+" ";
                    break;
                }
            }
        }
        //匹对完声母后如果字符串还么结束继续匹对韵母
        if(py.length()!=0){
            this.findym(py);
        }
    }
    public void findym(String py){
        ym.add("a");
        ym.add("e");
        ym.add("i");
        ym.add("o");
        ym.add("u");
        ym.add("v");
        ym.add("ai");
        ym.add("au");
        ym.add("ao");
        ym.add("ei");
        ym.add("ou");
        ym.add("ue");
        ym.add("ua");
        ym.add("an");
        ym.add("en");
        ym.add("in");
        ym.add("un");
        ym.add("ie");
        ym.add("uv");
        ym.add("uo");
        ym.add("iu");
        ym.add("ing");
        ym.add("ong");
        ym.add("eng");
        ym.add("ang");
        ym.add("uan");
        ym.add("ian");
        ym.add("iong");
        ym.add("iang");
        int temp = 0;
        char[] py2 = py.toCharArray();
        for(int i=0;i<ym.size();i++){
            for(int j=1;j<=py2.length;j++){
                String py3 = String.copyValueOf(py2, 0, j);
                if(py3.equals(ym.get(i))){
                    temp = ym.get(i).length();
                }
            }
        }
        if(temp!=0){
            result = result+String.copyValueOf(py2,0,temp)+" ";
            py = py.substring(temp);
        }
        if(py.length()!=0){
            py = py.trim();
            this.findsm(py);
        }
    }
    
    /**
     * @return
     * 		one result and clean
     */
    public String getOneSeg() {
    	String re = this.result;
    	this.result = "";
    	return re;
    }
    
    public String getFullSeg(String input) {
         String[] inputs = input.split("\'");
         StringBuffer buffer = new StringBuffer();
         for(String s : inputs){
         	this.findym(s);
         	buffer.append(this.getOneSeg().trim() + " ");
         }
         return buffer.toString();
	}
    
    
    public static void main(String[] args) throws IOException {
        Segment segment=new Segment();
        String input=new String("ping'an");
        String result = segment.getFullSeg(input);
        System.out.println(result);

    }
}

