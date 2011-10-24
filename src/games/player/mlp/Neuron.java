package games.player.mlp;

import java.util.Locale;
import java.util.Scanner;

public class Neuron {

	private double bias;

	private double[] weights;

	public static double tanh(double x) {
		return 2 / (1 + Math.exp(-2 * x)) - 1;
	}

	public double propagate(double[] input) {
		double activation = bias;
		for (int i = 0; i < input.length; i++) {
			activation += weights[i] * input[i];
		}
		return tanh(activation);
	}

	public static Neuron readFromString(String s, int numWeights) {
		Neuron neuron = new Neuron();
		Scanner scanner = new Scanner(s);
		scanner.useLocale(Locale.US);

		neuron.bias = scanner.nextDouble();
		neuron.weights = new double[numWeights];
		for (int w = 0; w < numWeights; w++) {
			neuron.weights[w] = scanner.nextDouble();
		}

		return neuron;
	}

}
