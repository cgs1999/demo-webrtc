package com.kedacom.webrtc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/** 
 * 对象序列化，反序列化（序列化对象转byte[],ByteBuffer, byte[]转object)
 * @author chengesheng@kedacom.com
 * @date 2013-10-14 下午8:35:07
 * @note ByteUtil.java
 */
public class ByteUtil {

	// 转换String为CharBuffer
	public static CharBuffer string2CharBuffer(String data) {
		return CharBuffer.wrap(data.toCharArray());
	}
	
	// 将字符转为字节(编码)
	public static byte[] getBytes(char[] chars) {
		Charset cs = Charset.forName("UTF-8");
		CharBuffer cb = CharBuffer.allocate(chars.length);
		cb.put(chars);
		cb.flip();
		ByteBuffer bb = cs.encode(cb);
		return bb.array();
	}

	// 将字节转为字符(解码)
	public static char[] getChars(byte[] bytes) {
		Charset cs = Charset.forName("UTF-8");
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		CharBuffer cb = cs.decode(bb);

		return cb.array();
	}

    public static byte[] getBytes(Serializable obj) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(obj);
        out.flush();
        byte[] bytes = bout.toByteArray();
        bout.close();
        out.close();
        return bytes;
    }
    
    public static int sizeof(Serializable obj) throws IOException {
        return getBytes(obj).length;
    }

    public static Object getObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
        ObjectInputStream oi = new ObjectInputStream(bi);
        Object obj = oi.readObject();
        bi.close();
        oi.close();
        return obj;
    }

    public static Object getObject(ByteBuffer byteBuffer) throws ClassNotFoundException, IOException {
        InputStream input = new ByteArrayInputStream(byteBuffer.array());
        ObjectInputStream oi = new ObjectInputStream(input);
        Object obj = oi.readObject();
        input.close();
        oi.close();
        byteBuffer.clear();
        return obj;
    }

    public static ByteBuffer getByteBuffer(Serializable obj) throws IOException {
        byte[] bytes = ByteUtil.getBytes(obj);
        ByteBuffer buff = ByteBuffer.wrap(bytes);
        return buff;
    }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        System.out.println(ByteUtil.sizeof(new Player1()));
        System.out.println(ByteUtil.sizeof(new Player2()));
        System.out.println(ByteUtil.sizeof(new Player3()));
        System.out.println(ByteUtil.sizeof(new Player4()));
        System.out.println(ByteUtil.sizeof(new Player5()));
        
        System.out.println("---------");
        
        Player5 p5 = new Player5();
        System.out.println(ByteUtil.sizeof(p5));
        p5.id1 = 100000;
        p5.id2 = 200000;
        System.out.println(ByteUtil.sizeof(p5));
        p5.name = "ooxx";
        System.out.println(ByteUtil.sizeof(p5));
        p5.name = "ooxxooxx";
        System.out.println(ByteUtil.sizeof(p5));
        
        System.out.println("---------");
        byte[] bytes = ByteUtil.getBytes(p5);
        Player5 p5_2 = (Player5) ByteUtil.getObject(bytes);
        System.out.println(p5_2.id1);
        System.out.println(p5_2.id2);
        System.out.println(p5_2.name);
        
        System.out.println("---------");
        System.out.println(ByteUtil.sizeof(new Player6()));
        Player6 p6 = new Player6();
        System.out.println(ByteUtil.sizeof(p6));
        p6.id1 = 100000;
        p6.id2 = 200000;
        System.out.println(ByteUtil.sizeof(p6));
        p6.setName("Vicky");
        System.out.println(ByteUtil.sizeof(p6));
        p6.setName("中文名称");
        System.out.println(ByteUtil.sizeof(p6));
        
        bytes = ByteUtil.getBytes(p6);
        Player6 p6_2 = (Player6) ByteUtil.getObject(bytes);
        System.out.println(p6_2.id1);
        System.out.println(p6_2.id2);
        System.out.println(p6_2.getName());
    }
}

class Player1 implements Serializable {
    int id1;
}

class Player2 extends Player1 {
    int id2;
}

class Player3 implements Serializable {
    int id1;
    int id2;
}

class Player4 extends Player3 {
    String name;
}

class Player5 implements Serializable {
    int id1;
    int id2;
    String name;
}

class Player6 implements Serializable {
    final static Charset chrarSet = Charset.forName("UTF-8");
    
    int id1;
    int id2;
    private byte[] name = new byte[20];

    public String getName() {
        return new String(name, chrarSet);
    }

    public void setName(String name) {
        this.name = name.getBytes(chrarSet);
    }
    
    // public void setName(String name) {
    //    byte[] tmpBytes = name.getBytes(chrarSet);
    //    for (int i = 0; i < tmpBytes.length; i++) {
    //        this.name[i] = tmpBytes[i];
    //    }
    // }
}