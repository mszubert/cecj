package framsticks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import cecj.interaction.FloatPairTestResult;
import cecj.interaction.TestResult;

import ec.EvolutionState;
import ec.util.Parameter;

public class FramsticksUtils {

	private static final String NEW_CMD = "%s \"getsimplest 1 %s\" -q";
	private static final String EVAL_CMD = "%s \"ex %s\" \"eval %s %s\" -q";
	private static final String MUTATE_CMD = "%s rnd mut -q < %s";
	private static final String XOVER_CMD = "%s rnd \"crossover %s %s\" -q";

	private static final String GENOTYPE_DESC = "org:\ngenotype:~\n%s~\n";
	private static final String TEMPORARY_FILE_NAME = "temp.gen";

	private static final String P_DIRECTORY_PATH = "directory-path";
	private static final String P_SCRIPTS_OUTPUT = "scripts-output";
	private static final String P_SETTINGS = "settings-file";
	private static final String P_WORKING_DIRECTORY = "working-directory";
	private static final String P_EXPERIMENT_DEFINITION = "expdef";
	private static final String P_EXECUTABLE_COMMAND = "executable-cmd";
	private static final String P_DEBUG = "debug";

	private String directoryPath;
	private String scriptsOutputPath;
	private String settingsFile;
	private String workingDirectory;
	private String experimentDefinition;
	private String executableCommand;
	private boolean debug;

	private static FramsticksUtils instance;

	public synchronized static FramsticksUtils getInstance(final EvolutionState state) {
		if (instance == null) {
			instance = new FramsticksUtils();
			instance.setup(state);
		}
		return instance;
	}

	private void setup(final EvolutionState state) {
		Parameter def = FramsticksDefaults.base();
		directoryPath = state.parameters.getString(null, def.push(P_DIRECTORY_PATH));
		if (directoryPath == null) {
			state.output.fatal("No Framsticks directory specified", def.push(P_DIRECTORY_PATH));
		}
		scriptsOutputPath = state.parameters.getString(null, def.push(P_SCRIPTS_OUTPUT));
		if (scriptsOutputPath == null) {
			state.output.fatal("No scripts output file specified", def.push(P_SCRIPTS_OUTPUT));
		}
		settingsFile = state.parameters.getString(null, def.push(P_SETTINGS));
		if (settingsFile == null) {
			state.output.fatal("No settings file specified", def.push(P_SETTINGS));
		}
		workingDirectory = state.parameters.getString(null, def.push(P_WORKING_DIRECTORY));
		if (workingDirectory == null) {
			state.output.fatal("No working directory specified", def.push(P_WORKING_DIRECTORY));
		}
		experimentDefinition = state.parameters.getString(null, def.push(P_EXPERIMENT_DEFINITION));
		if (experimentDefinition == null) {
			state.output.fatal("No experiment definition specified", def
					.push(P_EXPERIMENT_DEFINITION));
		}
		executableCommand = state.parameters.getString(null, def.push(P_EXECUTABLE_COMMAND));
		if (executableCommand == null) {
			state.output.fatal("No executable command specified", def.push(P_EXECUTABLE_COMMAND));
		}

		debug = state.parameters.getBoolean(null, def.push(P_DEBUG), false);
	}

	private String executeCommand(String command) {
		if (debug) {
			System.err.println("Executing command : " + command);
		}

		String result = new String();
		try {
			File f = new File(directoryPath);

			String[] cmd;
			String os = System.getProperty("os.name");
			if (os.contains("Linux")) {
				cmd = new String[] { "/bin/bash", "-c", directoryPath + command };
			} else if (os.contains("Windows")) {
				cmd = new String[] { "cmd.exe", "/C", directoryPath + command };
			} else {
				throw new Exception("Not supported OS");
			}

			Process p = Runtime.getRuntime().exec(cmd, null, f);

			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			result = readInput(input);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (debug) {
			System.err.println("Result : " + result);
		}

		return result;
	}

	private void saveToFile(String filePath, String contents) {
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(filePath));
			output.write(contents);
			output.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String readFromFile(String filePath) {
		String result = new String();
		try {
			BufferedReader input = new BufferedReader(new FileReader(filePath));
			result = readInput(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}

	private String readInput(BufferedReader input) {
		StringBuilder result = new StringBuilder();

		try {
			String line;
			while ((line = input.readLine()) != null) {
				result.append(line + '\n');
			}
			input.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// Delete last newline character
		if (result.length() > 0) {
			return result.substring(0, result.length() - 1);
		} else {
			return result.toString();
		}
	}

	public float evaluateGenotype(String genotype, String fileName) {
		String fileContents = String.format(GENOTYPE_DESC, genotype);
		String filePath = workingDirectory + fileName;

		saveToFile(filePath, fileContents);
		executeCommand(String.format(EVAL_CMD, executableCommand, experimentDefinition,
				settingsFile, filePath));
		String evaluation = readFromFile(scriptsOutputPath);

		return Float.parseFloat(evaluation.split("\t")[1]);
	}

	@Deprecated
	public TestResult pseudoCoevolutionaryEvaluate(String candidate, String test, String fileName) {
		float candidateResult = evaluateGenotype(candidate, fileName);
		float testResult = evaluateGenotype(test, fileName);

		return new FloatPairTestResult(candidateResult, testResult);
	}

	private static float chasingBest = 2.0f;
	private static float chasedBest = 2.0f;

	public TestResult coevolutionaryEvaluate(String candidate, String test, String fileName) {
		String fileContents = String.format(GENOTYPE_DESC, candidate) + "\n"
				+ String.format(GENOTYPE_DESC, test);
		String filePath = workingDirectory + fileName;
		saveToFile(filePath, fileContents);

		executeCommand(String.format(EVAL_CMD, executableCommand, experimentDefinition,
				settingsFile, filePath));
		String evaluation = readFromFile(scriptsOutputPath);

		float candidateResult = 0;
		float testResult = 0;

		try {
			String[] str = evaluation.split("\n");
			candidateResult = Float.parseFloat(str[0].split("\t")[1]); // chasing
			testResult = Float.parseFloat(str[1].split("\t")[1]); // chased

			chasingBest = ((candidateResult < chasingBest) ? (candidateResult) : (chasingBest));
			chasedBest = ((chasedBest < testResult) ? (chasedBest) : (testResult));

			System.out.printf("%7.4f %7.4f --> %7.4f %7.4f\n", candidateResult, chasingBest,
					testResult, chasedBest);
		} catch (Exception ex) {
			System.out.println(scriptsOutputPath
					+ " in bad format. There must be 2 lines with results.");
		}

		return new FloatPairTestResult(candidateResult, testResult);
	}

	public String mutateGenotype(String genotype) {
		String filePath = workingDirectory + TEMPORARY_FILE_NAME;
		saveToFile(filePath, genotype);
		return executeCommand(String.format(MUTATE_CMD, executableCommand, filePath));
	}

	public String crossoverGenotypes(String genotype1, String genotype2) {
		String filePath1 = workingDirectory + TEMPORARY_FILE_NAME;
		String filePath2 = workingDirectory + "_" + TEMPORARY_FILE_NAME;
		saveToFile(filePath1, genotype1);
		saveToFile(filePath2, genotype2);
		return executeCommand(String.format(XOVER_CMD, executableCommand, filePath1, filePath2));
	}

	public String getNewGenotype(int initializationType) {
		return executeCommand(String.format(NEW_CMD, executableCommand, initializationType));
	}

	/*
	 * Sample usage :
	 */
	/*
	 * public static void main(String[] args) { FramsticksUtils utils =
	 * FramsticksUtils.getInstance(); System.out.println(utils.evaluateGenotype("X", "halo1.gen"));
	 * System.out.println(utils.mutateGenotype("X"));
	 * System.out.println(utils.crossoverGenotypes("AX", "MX"));
	 * System.out.println(utils.getNewGenotype()); }
	 */
}