package com.zhidao.sgr.zhidao3d.image_handle;

public class MatrixCalculator  {
	public double m00;
	public double m01;
	public double m02;
	public double m10;
	public double m11;
	public double m12;
	public double m20;
	public double m21;
	public double m22;
	
    public MatrixCalculator() {
        m00 = m11 = m22 = 1.0;
        m01 = m02 = m10 = m12 = m20 = m21 = 0.0;
    }

    private static MatrixCalculator CreateObj(double x0, double y0, double x1,
			double y1, double x2, double y2, double x3, double y3) {
		MatrixCalculator matrix = new MatrixCalculator();
		setupValues(x0, y0, x1, y1, x2, y2, x3, y3, matrix);
		matrix.updatePoints();
		return matrix;
	}
	
	private static MatrixCalculator CreateObj2(double x0, double y0,
			double x1, double y1, double x2, double y2, double x3, double y3) {
		MatrixCalculator mc = new MatrixCalculator();
		setupValues(x0, y0, x1, y1, x2, y2, x3, y3, mc);
		return mc;
	}

	private final void updatePoints() {
		double m00p = m11 * m22 - m12 * m21;
		double m01p = m12 * m20 - m10 * m22;
		double m02p = m10 * m21 - m11 * m20;
		double m10p = m02 * m21 - m01 * m22;
		double m11p = m00 * m22 - m02 * m20;
		double m12p = m01 * m20 - m00 * m21;
		double m20p = m01 * m12 - m02 * m11;
		double m21p = m02 * m10 - m00 * m12;
		double m22p = m00 * m11 - m01 * m10;

		m00 = m00p;
		m01 = m10p;
		m02 = m20p;
		m10 = m01p;
		m11 = m11p;
		m12 = m21p;
		m20 = m02p;
		m21 = m12p;
		m22 = m22p;
	}

	private static final void setupValues(double x0, double y0, double x1,
			double y1, double x2, double y2, double x3, double y3,
			MatrixCalculator mc) {
		double dx3 = x0 - x1 + x2 - x3;
		double dy3 = y0 - y1 + y2 - y3;

		mc.m22 = 1.0F;

		if ((dx3 == 0.0F) && (dy3 == 0.0F)) {
			mc.m00 = x1 - x0;
			mc.m01 = x2 - x1;
			mc.m02 = x0;
			mc.m10 = y1 - y0;
			mc.m11 = y2 - y1;
			mc.m12 = y0;
			mc.m20 = 0.0F;
			mc.m21 = 0.0F;
		} else {
			double dx1 = x1 - x2;
			double dy1 = y1 - y2;
			double dx2 = x3 - x2;
			double dy2 = y3 - y2;

			double it = 1.0F / (dx1 * dy2 - dx2 * dy1);
			mc.m20 = (dx3 * dy2 - dx2 * dy3) * it;
			mc.m21 = (dx1 * dy3 - dx3 * dy1) * it;
			mc.m00 = x1 - x0 + mc.m20 * x1;
			mc.m01 = x3 - x0 + mc.m21 * x3;
			mc.m02 = x0;
			mc.m10 = y1 - y0 + mc.m20 * y1;
			mc.m11 = y3 - y0 + mc.m21 * y3;
			mc.m12 = y0;
		}
	}

	private void calculateInternal(MatrixCalculator mc) {
		double m00p = m00 * mc.m00 + m10 * mc.m01 + m20 * mc.m02;
		double m10p = m00 * mc.m10 + m10 * mc.m11 + m20 * mc.m12;
		double m20p = m00 * mc.m20 + m10 * mc.m21 + m20 * mc.m22;
		double m01p = m01 * mc.m00 + m11 * mc.m01 + m21 * mc.m02;
		double m11p = m01 * mc.m10 + m11 * mc.m11 + m21 * mc.m12;
		double m21p = m01 * mc.m20 + m11 * mc.m21 + m21 * mc.m22;
		double m02p = m02 * mc.m00 + m12 * mc.m01 + m22 * mc.m02;
		double m12p = m02 * mc.m10 + m12 * mc.m11 + m22 * mc.m12;
		double m22p = m02 * mc.m20 + m12 * mc.m21 + m22 * mc.m22;

		m00 = m00p;
		m10 = m10p;
		m20 = m20p;
		m01 = m01p;
		m11 = m11p;
		m21 = m21p;
		m02 = m02p;
		m12 = m12p;
		m22 = m22p;
	}

	public static MatrixCalculator calculate(double x0, double y0, double x1,
			double y1, double x2, double y2, double x3, double y3, double x_0,
			double y_0, double x_1, double y_1, double x_2, double y_2,
			double x_3, double y_3) {
		MatrixCalculator matrix1 = CreateObj(x0, y0, x1, y1, x2, y2, x3, y3);

		MatrixCalculator matrix2 = CreateObj2(x_0, y_0, x_1, y_1, x_2, y_2, x_3,
				y_3);

		matrix1.calculateInternal(matrix2);
		return matrix1;
	}
}
