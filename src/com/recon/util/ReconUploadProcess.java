package com.recon.util;

import java.io.File;
import java.util.Scanner;

public class ReconUploadProcess {
	
	public static void main(String[] args) {
		
		if(0<args.length) {
			
			String filename = args[0];
			File file= new File(filename);
			
		}
		
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter a file name: ");
		System.out.flush();
		String filename = scanner.nextLine();
		File file = new File(filename);
		System.out.println("Enter Date For File");
		String filedate = scanner.nextLine();
		
		
		if(file.renameTo(new File("D:\\Switch\\" + file.getName()))) {
		
			System.out.println("sl");
		}else {
			
			System.out.println("sl3wr4");
		}
			
		System.out.println(file.getName());
		System.out.println(filedate);
		
		
		
		
	}

}
