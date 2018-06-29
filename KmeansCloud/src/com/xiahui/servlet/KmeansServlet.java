package com.xiahui.servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xiahui.util.KMeansCluster;

import Decoder.BASE64Decoder;

/**
 * Servlet implementation class KmeansServlet
 */
@WebServlet("/KmeansServlet")
public class KmeansServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public KmeansServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		OutputStream out  = response.getOutputStream();
		
		// 获取传递的值
		String photo = request.getParameter("photo");	// 图片数据
		String k = request.getParameter("k");	// Kmeans算法分类数
		String m = request.getParameter("m");	// Kmeans算法迭代数
		String speedValue = request.getParameter("speedValue");		// 测试数据

		System.out.println("k=" + k + " & m=" + m + " & photo=" + photo);
		
		if(Integer.parseInt(k) == 0) {
			// 网速测试
			out.write(speedValue.getBytes());
		}else {
			// 执行KMeans算法
			long startTime = System.nanoTime();		// 开始时间
			// Base64 解码
			Decoder decoder = Base64.getUrlDecoder();
			byte[] bytes = decoder.decode(photo);
			// 将解码后的图片数据利用BufferedImage存储，方便KMeans算法执行
			InputStream input = new ByteArrayInputStream(bytes, 0, bytes.length);
			BufferedImage image = ImageIO.read(input);
			input.close();
			// 执行KMeans算法，处理图片
			KMeansCluster cluster = new KMeansCluster();
			image = cluster.getResultGraphics(image, Integer.parseInt(k), Integer.parseInt(m));
			// 将图片由BufferedImage 转化为 byte数组，方便网络传输
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			ImageIO.write(image, "png", bos);
			bytes = bos.toByteArray();
			bos.close();
			
			long endTime = System.nanoTime();	// 结束时间
			long cloudExecuteTime = (endTime - startTime) / 1000000;	// 执行消耗的时间
			System.out.println("k = " + k + "  m = " + m + "photo = " + bytes.toString() + "  cloudExecuteTime = " + cloudExecuteTime);
			// 将图片写入D盘，命名为after.png
			/*File file = new File("d:","after.png");
			if(!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream out1 = new FileOutputStream(file);
			out1.write(bytes);
			out1.flush();
			out1.close();*/
			
			out.write(bytes);
		}
	}

}
