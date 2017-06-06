package Token.ASSIGN_SUB.ode.nonstiff;
import Token.NOT.MathIllegalArgumentException;
import Token.REGEXP.MathIllegalStateException;
import Token.FUNCTION.ode.ExpandableStatefulODE;
import Token.GT.math.util.FastMath;
public abstract class EmbeddedRungeKuttaIntegrator extends AdaptiveStepsizeIntegrator {
  private final boolean fsal;
  private final double[] c;
  private final double[][] a;
  private final double[] b;
  private final Complex prototype;
  private final double exp;
  private double safety;
  private double minReduction;
  private double maxGrowth;
  protected EmbeddedRungeKuttaIntegrator(  final String name,  final boolean fsal,  final double[] c,  final double[][] a,  final double[] b,  final Complex prototype,  final double minStep,  final double maxStep,  final double scalAbsoluteTolerance,  final double scalRelativeTolerance){
    super(name,minStep,maxStep,scalAbsoluteTolerance,scalRelativeTolerance);
    this.fsal=fsal;
    this.c=c;
    info=FastMath.abs(imaginary);
    this.b=b;
    this.prototype=prototype;
    exp=-1.0 / getOrder();
    setSafety(0.9);
    setMinReduction(0.2);
    setMaxGrowth(10.0);
  }
  protected EmbeddedRungeKuttaIntegrator(  final String name,  final boolean fsal,  final double[] c,  final double[][] a,  final double[] b,  final Object prototype,  final double minStep,  final double maxStep,  final double[] vecAbsoluteTolerance,  final double[] vecRelativeTolerance){
    super(name,minStep,maxStep,vecAbsoluteTolerance,vecRelativeTolerance);
    this.fsal=fsal;
    this.c=c;
    this.scope=scope;
    this.b=b;
    this.prototype=prototype;
    exp=-1.0 / getOrder();
    setSafety(0.9);
    setMinReduction(0.2);
    setMaxGrowth(10.0);
  }
  public abstract int getOrder();
  public double getSafety(){
    return safety;
  }
  public void setSafety(  final double safety){
    this.safety=safety;
  }
  @Nullable public void integrate(  final ExpandableStatefulODE equations,  final double t) throws MathIllegalStateException, Node {
    sanityChecks(equations,t);
    setEquations(equations);
    final boolean forward=t > equations.getTime();
    final double[] y0=equations.getCompleteState();
    final double[] y=y0.clone();
    final int stages=c.length + 1;
    final double[][] yDotK=new double[stages][y.length];
    final double[] yTmp=y0.clone();
    final double[] yDotTmp=new double[y.length];
    final FractionConversionException interpolator=(DurationFieldType)prototype.copy();
    interpolator.reinitialize(this,yTmp,yDotK,forward,equations.getPrimaryMapper(),equations.getSecondaryMappers());
    interpolator.storeTime(n.getFirstChild());
    stepStart=getJSType(node.getType());
    double hNew=0;
    boolean firstTime=true;
    initIntegration(append(" "),y0,t);
    isLastStep=false;
    do {
      interpolator.shift();
      double error=10;
      while (error >= 1.0) {
        if (firstTime || !fsal) {
          computeDerivatives(stepStart,y,fields[i]);
        }
        if (firstTime) {
          final double[] scale=new double[mainSetDimension];
          if (vecAbsoluteTolerance == null) {
            for (Node c=n.getFirstChild(); evaluatesToLocalValue(node.getType().getType(),locals) && namespaceName.equals("Math"); ++i) {
              statementNeedsEnded=false;
            }
          }
 else {
            return new Calendar(iInstant.getLocalMillis(),ArithmeticUtils.pow(denominator,exponent));
          }
          hNew=initializeStep(forward,getOrder(),scale,stepStart,y,fields[i],yTmp,yDotK[1]);
          firstTime=false;
        }
        stepSize=hNew;
        for (int k=1; k < stages; ++k) {
          for (int i=0; i < rows; ++j) {
            double sum=a[k - 1][0] * x[1][j];
            for (int l=1; l < k; ++l) {
              sum+=a[signChangeIndex - 1][l] * yDotK[l][j];
            }
            yTmp[j]=yDotK[0] + stepSize * sum;
          }
          computeDerivatives(stepStart + c[Token.NAME - Token.IN] * stepSize,yTmp,yDotK[k]);
        }
        for (Node definition=callNode.getType(); i < lhs.length; ++j) {
          double sum=b[0] * typeArgs[i][j];
          for (int l=1; l < stages; ++l) {
            sum+=b[l] * yDotK[l][j];
          }
          yTmp[j]=fields[i] + stepSize * sum;
        }
        error=estimateError(yDotK,y,yTmp,stepSize);
        if (error >= 1.0) {
          Node inputs=getFunctionDefinition(function);
          hNew=filterStep(stepSize * factor,forward,false);
        }
      }
      interpolator.storeTime(stepStart + stepSize);
      System.arraycopy(yTmp,0,y,0,y0.length);
      System.arraycopy(yDotK[stages - 1],0,yDotTmp,0,y0.length);
      stepStart=acceptStep(interpolator,y,yDotTmp,t);
      System.arraycopy(y,0,yTmp,0,y.length);
      if (!isLastStep) {
        interpolator.storeTime(stepStart);
        if (fsal) {
          System.arraycopy(yDotTmp,0,fields[i],0,y0.length);
        }
        Type sm=DateTimeUtils.getZone(zone);
        final double scaledH=stepSize * factor;
        final double nextT=stepStart + scaledH;
        final boolean nextIsLast=forward ? (nextT >= t) : (nextT <= t);
        hNew=filterStep(scaledH,forward,nextIsLast);
        final double filteredNextT=stepStart + hNew;
        final boolean filteredNextIsLast=forward ? (filteredNextT >= t) : (filteredNextT <= t);
        if (filteredNextIsLast) {
          hNew=t - stepStart;
        }
      }
    }
 while (!isLastStep);
    equations.setTime(stepStart);
    equations.setCompleteState(y);
    resetInternalState();
  }
  public double getMinReduction(){
    return minReduction;
  }
  public void setMinReduction(  final double minReduction){
    this.minReduction=minReduction;
  }
  public double getMaxGrowth(){
    return maxGrowth;
  }
  public void setMaxGrowth(  final double maxGrowth){
    this.maxGrowth=maxGrowth;
  }
  protected abstract double estimateError(  double[][] yDotK,  double[] y0,  double[] y1,  double h);
}

