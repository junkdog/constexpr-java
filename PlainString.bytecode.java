// class version 52.0 (52)
// access flags 0x21
public class net/onedaybeard/constexpr/model/PlainString {

  // compiled from: PlainString.java

  // access flags 0x19
  public final static Ljava/lang/String; s1 = "hmmhoho"
  @Lnet/onedaybeard/constexpr/ConstExpr;() // invisible

  // access flags 0x19
  public final static Ljava/lang/String; s2
  @Lnet/onedaybeard/constexpr/ConstExpr;() // invisible

  // access flags 0x1
  public <init>()V
   L0
    LINENUMBER 5 L0
    ALOAD 0
    INVOKESPECIAL java/lang/Object.<init> ()V
    RETURN
   L1
    LOCALVARIABLE this Lnet/onedaybeard/constexpr/model/PlainString; L0 L1 0
    MAXSTACK = 1
    MAXLOCALS = 1

  // access flags 0xA
  private static hnn(I)Ljava/lang/String;
   L0
    LINENUMBER 10 L0
    LDC "-uh-"
    ARETURN
   L1
    LOCALVARIABLE ignored I L0 L1 0
    MAXSTACK = 1
    MAXLOCALS = 1

  // access flags 0x8
  static <clinit>()V
   L0
    LINENUMBER 7 L0
    NEW java/lang/StringBuilder
    DUP
    INVOKESPECIAL java/lang/StringBuilder.<init> ()V
    LDC "hmm"
    INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
    LDC 57005
    INVOKESTATIC net/onedaybeard/constexpr/model/PlainString.hnn (I)Ljava/lang/String;
    INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
    LDC "hoho"
    INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
    INVOKEVIRTUAL java/lang/StringBuilder.toString ()Ljava/lang/String;
    PUTSTATIC net/onedaybeard/constexpr/model/PlainString.s2 : Ljava/lang/String;
    RETURN
    MAXSTACK = 2
    MAXLOCALS = 0
}
