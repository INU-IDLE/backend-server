package com.idle.rushcutter;

import org.springframework.boot.SpringApplication;

public class TestRushcutterApplication {

	public static void main(String[] args) {
		SpringApplication.from(RushcutterApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
