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
		
		// ��ȡ���ݵ�ֵ
		String photo = request.getParameter("photo");	// ͼƬ����
		String k = request.getParameter("k");	// Kmeans�㷨������
		String m = request.getParameter("m");	// Kmeans�㷨������
		String speedValue = request.getParameter("speedValue");		// ��������

		System.out.println("k=" + k + " & m=" + m + " & photo=" + photo);
		
		if(Integer.parseInt(k) == 0) {
			// ���ٲ���
			out.write(speedValue.getBytes());
		}else {
			// ִ��KMeans�㷨
			long startTime = System.nanoTime();		// ��ʼʱ��
			// Base64 ����
			Decoder decoder = Base64.getUrlDecoder();
			byte[] bytes = decoder.decode(photo);
			// ��������ͼƬ��������BufferedImage�洢������KMeans�㷨ִ��
			InputStream input = new ByteArrayInputStream(bytes, 0, bytes.length);
			BufferedImage image = ImageIO.read(input);
			input.close();
			// ִ��KMeans�㷨������ͼƬ
			KMeansCluster cluster = new KMeansCluster();
			image = cluster.getResultGraphics(image, Integer.parseInt(k), Integer.parseInt(m));
			// ��ͼƬ��BufferedImage ת��Ϊ byte���飬�������紫��
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			ImageIO.write(image, "png", bos);
			bytes = bos.toByteArray();
			bos.close();
			
			long endTime = System.nanoTime();	// ����ʱ��
			long cloudExecuteTime = (endTime - startTime) / 1000000;	// ִ�����ĵ�ʱ��
			System.out.println("k = " + k + "  m = " + m + "photo = " + bytes.toString() + "  cloudExecuteTime = " + cloudExecuteTime);
			// ��ͼƬд��D�̣�����Ϊafter.png
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
