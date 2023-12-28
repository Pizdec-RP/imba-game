package net.pzdcrp.Aselia.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class MathU {
    private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
    private static final double FRAC_BIAS = Double.longBitsToDouble(4805340802404319232L);
    private static final double[] ASIN_TAB = new double[257];
    private static final double[] COS_TAB = new double[257];
	private static final float[] SIN = make(new float[65536], arrf -> {
        for (int i = 0; i < arrf.length; ++i) {
            arrf[i] = (float)Math.sin(i * 3.141592653589793 * 2.0 / 65536.0);
        }
    });
	private static Random rnd = new Random();

	public static int floorDouble(double n) {
        int i = (int) n;
        return n >= i ? i : i - 1;
    }


    public static int ceilDouble(double n) {
        int i = (int) (n + 1);
        return n >= i ? i : i - 1;
    }

    public static int floor(float value) {
        int i = (int)value;
        return value < (float)i ? i - 1 : i;
    }

    public static int ceilFloat(float n) {
        int i = (int) (n + 1);
        return n >= i ? i : i - 1;
    }

    public static int xz(double n) {
    	int i = (int)n + 1;
    	return i > n ? i : (int)n;
    }

	public static double atan2(double d, double d2) {
        boolean bl;
        double d3;
        boolean bl2;
        boolean bl3;
        double d4 = d2 * d2 + d * d;
        if (Double.isNaN(d4)) {
            return Double.NaN;
        }
        bl2 = d < 0.0;
        if (bl2) {
            d = -d;
        }
        bl = d2 < 0.0;
        if (bl) {
            d2 = -d2;
        }
        bl3 = d > d2;
        if (bl3) {
            d3 = d2;
            d2 = d;
            d = d3;
        }
        d3 = fastInvSqrt(d4);
        double d5 = FRAC_BIAS + (d *= d3);
        int n = (int)Double.doubleToRawLongBits(d5);
        double d6 = ASIN_TAB[n];
        double d7 = COS_TAB[n];
        double d8 = d5 - FRAC_BIAS;
        double d9 = d * d7 - (d2 *= d3) * d8;
        double d10 = (6.0 + d9 * d9) * d9 * 0.16666666666666666;
        double d11 = d6 + d10;
        if (bl3) {
            d11 = 1.5707963267948966 - d11;
        }
        if (bl) {
            d11 = 3.141592653589793 - d11;
        }
        if (bl2) {
            d11 = -d11;
        }
        return d11;
    }

	public static int wrapDegrees(int n) {
        int n2 = n % 360;
        if (n2 >= 180) {
            n2 -= 360;
        }
        if (n2 < -180) {
            n2 += 360;
        }
        return n2;
    }

    public static float wrapDegrees(float f) {
        float f2 = f % 360.0f;
        if (f2 >= 180.0f) {
            f2 -= 360.0f;
        }
        if (f2 < -180.0f) {
            f2 += 360.0f;
        }
        return f2;
    }

    public static double wrapDegrees(double d) {
        double d2 = d % 360.0;
        if (d2 >= 180.0) {
            d2 -= 360.0;
        }
        if (d2 < -180.0) {
            d2 += 360.0;
        }
        return d2;
    }

	public static float fastInvSqrt(float f) {
        float f2 = 0.5f * f;
        int n = Float.floatToIntBits(f);
        n = 1597463007 - (n >> 1);
        f = Float.intBitsToFloat(n);
        f *= 1.5f - f2 * f * f;
        return f;
    }

	public static double fastInvSqrt(double d) {
        double d2 = 0.5 * d;
        long l = Double.doubleToRawLongBits(d);
        l = 6910469410427058090L - (l >> 1);
        d = Double.longBitsToDouble(l);
        d *= 1.5 - d2 * d * d;
        return d;
    }

	public static double getDir(double d) {
		if (d < 0) return -1;
		else if (d > 0) return 1;
		else return 0;
	}

	public static <T> T make(T t, Consumer<T> consumer) {
        consumer.accept(t);
        return t;
    }

	public static int rndi(int min, int max) {
	    return rnd.nextInt(max - min + 1) + min;
	}

	public static float rndf(float min, float max) {
		return min + rndnrm() * (max - min);
	}

	public static double rndd(double min, double max) {
		return min + rndnrm() * (max - min);
	}

	public static <M> M random(List<M> list) {
		return list.get(rndi(0,list.size()-1));
	}

	public static float sin(float f) {
        return SIN[(int)(f * 10430.378f) & 0xFFFF];
    }

    public static float cos(float f) {
        return SIN[(int)(f * 10430.378f + 16384.0f) & 0xFFFF];
    }

	public static double fround(float f) {
		return f;
	}

	public static double round(double numberToRound, int decimalPlaces) {
		if(Double.isNaN(numberToRound))
			return Double.NaN;

		double factor = 1;
		for(int i = 0; i < Math.abs(decimalPlaces); i++)
			if(decimalPlaces > 0)
				factor *= 10;
			else
				factor /= 10;

		return Math.round(numberToRound*factor)/factor;
	}

	public static int clamp(int num, int min, int max) {
        if (num < min) {
            return min;
        } else {
            return num > max ? max : num;
        }
    }

    /**
     * Returns the value of the first parameter, clamped to be within the lower and upper limits given by the second and
     * third parameters
     */
    public static float clamp(float num, float min, float max) {
        if (num < min) {
            return min;
        } else {
            return num > max ? max : num;
        }
    }

    public static double clamp(double num, double min, double max) {
        if (num < min) {
            return min;
        } else {
            return num > max ? max : num;
        }
    }

    public static int ceil(float value) {
        int i = (int)value;
        return value > i ? i + 1 : i;
    }

    public static int ceil(double value) {
        int i = (int)value;
        return value > i ? i + 1 : i;
    }

    public static double Truncate(double value) {
    	if (value < 0) {
            return Math.ceil(value);
        } else {
            return Math.floor(value);
        }
    }
    
    public static float max(float a, float b) {
		return a > b ? a : b;
	}

	public static int max(int[] arr) {
		int max = Integer.MIN_VALUE;
		for (int i : arr) {
			if (i > max) i = max;
		}
		return max;
	}

	public static int[][][] deepCopy3DArray(int[][][] original) {
	    int[][][] copy = new int[original.length][][];

	    for (int i = 0; i < original.length; i++) {
	        copy[i] = new int[original[i].length][];

	        for (int j = 0; j < original[i].length; j++) {
	            // Копирование каждого подмассива
	            copy[i][j] = Arrays.copyOf(original[i][j], original[i][j].length);
	        }
	    }

	    return copy;
	}

	public static int diap(int min, int max, double scl) {
		return (int) ((max - min) * scl) + min;
	}

	public static double lerp(double minValue, double maxValue, double t) {
	    return minValue + (maxValue - minValue) * t;
	}
	public static float lerp(float minValue, float maxValue, float t) {
	    return minValue + (maxValue - minValue) * t;
	}

	public static double norm(double minValue, double maxValue, double value) {
        if (minValue == maxValue) {
            return .0d;
        }
        return (value - minValue) / (maxValue - minValue);
    }

	public static float norm(float minValue, float maxValue, float value) {
        if (minValue == maxValue) {
            return .0f;
        }
        return (value - minValue) / (maxValue - minValue);
    }


	public static float rndnrm() {
		return rnd.nextFloat();
	}

	public static int alignPower(int input) {
	    if (isPowerOfTwoSquare(input)) {
	        return input;
	    }

	    int power = 1;
	    while (power <= input) {
	        power *= 2;
	    }
	    return power;
	}

	public static boolean isPowerOfTwoSquare(int n) {
	    int sqrt = (int) Math.sqrt(n);
	    return sqrt * sqrt == n;
	}


	public static float sqrt(float f) {
		return f*f;
	}


	public static float pow(final float base, final int power) {
	    float result = 1;
	    for( int i = 0; i < power; i++ ) {
	        result *= base;
	    }
	    return result;
	}
}
