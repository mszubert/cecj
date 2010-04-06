package ec.display;

import java.io.File;

import ec.display.Console;

public class CommandLineConsoleWrapper {
	
	public static void main(String[] args) {
		if (args.length > 0) {
			String file = args[0];
			String[] restArgs = new String[args.length - 1];
			for (int i = 1; i < args.length; i++) {
				restArgs[i - 1] = args[i];
			}

			Console application = new Console(restArgs);
			application.setVisible(true);
			openFile(application, file);
		} else {
			Console application = new Console(args);
			application.setVisible(true);
		}
	}

	private static void openFile(Console application, String file) {
		File f = new File(file);
		application.loadParameters(f);
		application.playButton.setEnabled(true);
		application.stepButton.setEnabled(true);
		application.conPanel.enableControls();
	}
}
