package com.pinganJava;
import java.io.*;
import java.util.UUID;

public class createData {
    public static void main(String[] args) {
        FileInputStream fis = null;//文件输入流
        FileOutputStream fos = null;//文件输出流
        InputStreamReader isr = null;
        OutputStreamWriter osw = null;
        BufferedReader br = null;
        BufferedWriter bw =null;
        try {
            String str = "";
            String str1 = "";
            String str2 = "";
            int rand;
            fis = new FileInputStream("C:\\Users\\33063\\Desktop\\test1.log");// 读入本地文件
            fos = new FileOutputStream("C:\\Users\\33063\\Desktop\\test1uuid.log");//写入本地文件

            isr = new InputStreamReader(fis);// InputStreamReader
            osw = new OutputStreamWriter(fos,"UTF-8");//写入文件编码格式为UTF-8

            br = new BufferedReader(isr);// 从字符输入流中读取文件中的内容
            bw = new BufferedWriter(osw);//将字符输出流写入到文件中

            String uuid  = UUID.randomUUID().toString();
            StringBuffer strb= new StringBuffer();//定义一个可变类型的String

            while ((str = br.readLine()) != null) {
                str1 = ";["+uuid + "\n";//读出对应标识ID
                str2=strb.append(str).append(str1).toString();//当读取的一行不为空时,把读到的str和str1进行连接并把值赋给str2
            }
            System.out.println(str2);// 打印出str2
            bw.write(str2+"\n");//写入文件

        } catch (FileNotFoundException e) {
            System.out.println("找不到指定文件");
        } catch (IOException e) {
            System.out.println("读取/写入文件失败");
        } finally {
            try {
                br.close();
                isr.close();
                fis.close();
                bw.close();
                osw.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
