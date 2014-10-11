package com.webmoli.loadtest;

import org.junit.Test;

public class LoadRunnerMainTest {

	@Test
	public void testLoadRunner() {
		String args[] = { "har-file/demo.di.com_1.har", "4", "demo.di.com" };
		LoadRunnerMain.main(args);
	}

}
