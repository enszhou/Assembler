package ustc.zes;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        int rowIndex = 0, origin = 0, pc = 0, pass = 0;
        String clause = "";
        String[] clauseBlock;
        String filePath, absoluteFilenameWithoutEx;
        HashMap<String, Integer> Ins = new HashMap<String, Integer>();
        HashMap<String, Integer> Label = new HashMap<String, Integer>();
        HashMap<String, String> Reg = new HashMap<String, String>();
        HashMap<String, String> SpecialIns = new HashMap<String, String>();
        LinkedList<String> ErrorMessage1 = new LinkedList<String>();
        LinkedList<String> ErrorMessage2 = new LinkedList<String>();

        Ins.put("ADD", 1);
        Ins.put("add", 1);
        Ins.put("AND", 5);
        Ins.put("and", 5);
        Ins.put("BR", 0);
        Ins.put("BRN", 0);
        Ins.put("BRZ", 0);
        Ins.put("BRP", 0);
        Ins.put("BRNZ", 0);
        Ins.put("BRNP", 0);
        Ins.put("BRZP", 0);
        Ins.put("BRNZP", 0);
        Ins.put("JMP", 12);
        Ins.put("jmp", 12);
        Ins.put("JSR", 4);
        Ins.put("jsr", 4);
        Ins.put("JSRR", 4);
        Ins.put("jsrr", 4);
        Ins.put("LD", 2);
        Ins.put("ld", 2);
        Ins.put("LDI", 10);
        Ins.put("ldi", 10);
        Ins.put("LDR", 6);
        Ins.put("ldr", 6);
        Ins.put("LEA", 14);
        Ins.put("lea", 14);
        Ins.put("NOT", 9);
        Ins.put("not", 9);
        Ins.put("RET", 12);
        Ins.put("ret", 12);
        Ins.put("RTI", 8);
        Ins.put("rti", 8);
        Ins.put("ST", 3);
        Ins.put("st", 3);
        Ins.put("STI", 11);
        Ins.put("sti", 11);
        Ins.put("STR", 7);
        Ins.put("str", 7);
        Ins.put("TRAP", 15);
        Ins.put("GETC", 15);
        Ins.put("OUT", 15);
        Ins.put("PUTS", 15);
        Ins.put("IN", 15);
        Ins.put("PUTSP", 15);
        Ins.put("HALT", 15);
        Ins.put(".FILL", 16);
        Ins.put(".BLKW", 17);
        Ins.put(".STRINGZ", 18);
        Ins.put(".ORIG", 19);
        Ins.put(".END", 20);
        Ins.put("R0", 25);
        Ins.put("R1", 25);
        Ins.put("R2", 25);
        Ins.put("R3", 25);
        Ins.put("R4", 25);
        Ins.put("R5", 25);
        Ins.put("R6", 25);
        Ins.put("R7", 25);

        Reg.put("R0", "000");
        Reg.put("R1", "001");
        Reg.put("R2", "010");
        Reg.put("R3", "011");
        Reg.put("R4", "100");
        Reg.put("R5", "101");
        Reg.put("R6", "110");
        Reg.put("R7", "111");

        SpecialIns.put("BR", "111");
        SpecialIns.put("BRN", "100");
        SpecialIns.put("BRZ", "010");
        SpecialIns.put("BRP", "001");
        SpecialIns.put("BRNZ", "110");
        SpecialIns.put("BRNP", "101");
        SpecialIns.put("BRZP", "011");
        SpecialIns.put("BRNZP", "111");
        SpecialIns.put("GETC", "1111000000100000");
        SpecialIns.put("OUT", "1111000000100001");
        SpecialIns.put("PUTS", "1111000000100010");
        SpecialIns.put("IN", "1111000000100011");
        SpecialIns.put("PUTSP", "1111000000100100");
        SpecialIns.put("HALT", "1111000000100101");
        SpecialIns.put("RET", "1100000111000000");

        try {
            File srcFile = new File(args[0]);
            filePath = srcFile.getCanonicalPath();
            absoluteFilenameWithoutEx = filePath.substring(0, filePath.lastIndexOf('.'));
            FileReader fileReader = new FileReader(srcFile);
            FileWriter binFileWriter = new FileWriter(absoluteFilenameWithoutEx + ".bin");
            FileWriter symFileWriter = new FileWriter(absoluteFilenameWithoutEx + ".sym");
            FileWriter lstFileWriter = new FileWriter(absoluteFilenameWithoutEx + "_temp.asm");
            FileOutputStream objStream = new FileOutputStream(absoluteFilenameWithoutEx + ".obj");
            BufferedReader br = new BufferedReader(fileReader);
            BufferedWriter binWriter = new BufferedWriter(binFileWriter);
            BufferedWriter symWriter = new BufferedWriter(symFileWriter);
            BufferedWriter lstWriter = new BufferedWriter(lstFileWriter);
            DataOutputStream objWriter = new DataOutputStream(objStream);

            rowIndex = 0;
            origin = 0;
            System.out.println("Starting Pass 1...");
            int j;
            while (true) {
                clause = br.readLine();
                if (clause == null) {
                    ErrorMessage1.addLast(".END Expected");
                    break;
                }
                //delete the blank line
                if (clause.equals(""))
                    continue;
                //System.out.println(clause);
                //去注释
                clauseBlock = clause.split("\\s*;", 2);
                if (clauseBlock[0].equals(""))
                    continue;
                clause = clauseBlock[0];
                //去开头空格
                clauseBlock = clause.split("\\s+", 2);
                if (clauseBlock[0].equals(""))
                    clause = clauseBlock[1];
                //instruction or label or op
                clauseBlock = clause.split("\\s*,\\s*|\\s+", 0);

                //check .ORIG
                if (rowIndex == 0) {
                    if (clauseBlock[0].toUpperCase().equals(".ORIG") && clauseBlock.length == 2) {
                        origin = String2Int(clauseBlock[1]);
                        rowIndex = origin;
                        lstWriter.write(clauseBlock[0].toUpperCase() + " #" + origin);
                        lstWriter.newLine();
                        continue;
                    }
                    else {
                        ErrorMessage1.addLast("Expected .ORIG");
                        break;
                    }
                }

                //check .END
                if (clauseBlock[0].equals(".END"))
                    break;

                //get the label and normalize the instruction
                String first = clauseBlock[0];
                String first_Uppercase = first.toUpperCase();

                if (Ins.containsKey(first_Uppercase) || first_Uppercase.matches("BRN?Z?P?")) {
                    j = 0;
                }
                else {
                    if (!Label.containsKey(first) && first.matches("[a-zA-Z][a-zA-Z0-9_]{0,19}") && !first.matches("([xX]-?(\\d|[AaBbCcDdEeFf])+)|([bB]-?[01]+)|(#-?[0-9])")) {
                        Label.put(first, rowIndex);
                        symWriter.write(first + "    " + rowIndex);
                        symWriter.newLine();
                        j = 1;
                    }
                    else {
                        ErrorMessage1.addLast("Unrecognized opcode or Syntax error");
                        continue;
                    }
                }

                for (int i = j; i < clauseBlock.length; i++) {
                    if (Ins.containsKey(clauseBlock[i].toUpperCase()))
                        lstWriter.write(clauseBlock[i].toUpperCase() + " ");
                    else {
                        if (clauseBlock[i].matches("([xX]-?(\\d|[AaBbCcDdEeFf])+)|([bB]-?[01]+)")) {
                            clauseBlock[i] = "#" + String2Int(clauseBlock[i]);
                        }
                        lstWriter.write(clauseBlock[i] + " ");
                    }
                }

                if (clauseBlock.length > 1 || j == 0) {
                    if (clauseBlock[j].equals(".BLKW")) {
                        rowIndex += String2Int(clauseBlock[j + 1]);
                    }
                    else if (clauseBlock[j].equals(".STRINGZ")) {
                        rowIndex += clauseBlock[j + 1].length() - 1;
                    }
                    else
                        rowIndex++;
                    lstWriter.newLine();
                }
            }

            if (!ErrorMessage1.isEmpty()) {
                pass = 0;
                throw new Exception("Pass 1 - " + ErrorMessage1.size() + " error(s)");
            }
            else {
                pass = 1;
                System.out.println("Pass 1 is OK \nStarting Pass 2...");
            }

            lstWriter.close();
            symWriter.close();
            br.close();
            lstFileWriter.close();
            symFileWriter.close();
            fileReader.close();
            FileReader asmFileReader = new FileReader(absoluteFilenameWithoutEx + "_temp.asm");
            BufferedReader asmReader = new BufferedReader(asmFileReader);
            int opcode;

            String ins;
            pc = origin;

            //.ORIG
            clause = asmReader.readLine();
            ins = Int2BinStr(origin, 16, false);
            binWriter.write(ins);
            binWriter.newLine();
            objWriter.writeShort(String2Short(ins));

            while ((clause = asmReader.readLine()) != null) {  //assemble
                clauseBlock = clause.split("\\s", 0);
                if (Ins.containsKey(clauseBlock[0])) {
                    opcode = Ins.get(clauseBlock[0]);
                    if (opcode < 16) {
                        pc++;
                        ins = Int2BinStr(opcode, 4, false);
                        switch (opcode) {
                            case 0:
                                ins = ins + SpecialIns.get(clauseBlock[0]);
                                if (Label.containsKey(clauseBlock[1])) {
                                    ins = ins + Int2BinStr(Label.get(clauseBlock[1]) - pc, 9, true);
                                }
                                else {
                                    ins = ins + Int2BinStr(clauseBlock[1], 9, true);
                                }
                                break;
                            case 1:
                            case 5:
                                if (Ins.containsKey(clauseBlock[3])) {
                                    ins = ins + Reg.get(clauseBlock[1]) + Reg.get(clauseBlock[2]) + "000" + Reg.get(clauseBlock[3]);
                                }
                                else {
                                    ins = ins + Reg.get(clauseBlock[1]) + Reg.get(clauseBlock[2]) + "1" + Int2BinStr(clauseBlock[3], 5, true);
                                }
                                break;
                            case 2:
                            case 3:
                            case 10:
                            case 11:
                                ins = ins + Reg.get(clauseBlock[1]) + Int2BinStr(Label.get(clauseBlock[2]) - pc, 9, true);
                                break;
                            case 4:
                                if (clauseBlock[0].equals("JSR")) {
                                    ins = ins + "1" + Int2BinStr(Label.get(clauseBlock[1]) - pc, 11, true);
                                }
                                else {
                                    ins = ins + "000" + Reg.get(clauseBlock[1]) + "000000";
                                }
                                break;
                            case 6:
                            case 7:
                                ins = ins + Reg.get(clauseBlock[1]) + Reg.get(clauseBlock[2]) + Int2BinStr(clauseBlock[3], 6, true);
                                break;
                            case 8:
                                ins = ins + "000000000000";
                                break;
                            case 9:
                                ins = ins + Reg.get(clauseBlock[1]) + Reg.get(clauseBlock[2]) + "111111";
                                break;
                            case 12:
                                if (clauseBlock.length == 1)
                                    ins = SpecialIns.get("RET");
                                else
                                    ins = ins + "000" + Reg.get(clauseBlock[1]) + "000000";
                                break;
                            case 13:
                                break;
                            case 14:
                                if (Label.containsKey(clauseBlock[2])) {
                                    ins = ins + Reg.get(clauseBlock[1]) + Int2BinStr(Label.get(clauseBlock[2]) - pc, 9, true);
                                }
                                else {
                                    ins = ins + Reg.get(clauseBlock[1]) + Int2BinStr(clauseBlock[2], 9, true);
                                }
                                break;
                            case 15:
                                if (clauseBlock.length == 2)
                                    ins = ins + "0000" + Int2BinStr(clauseBlock[1], 8, false);
                                else
                                    ins = SpecialIns.get(clauseBlock[0]);
                                break;
                        }
                        //write in
                        if (ins != null && ins.length() == 16) {
                            binWriter.write(ins);
                            binWriter.newLine();
                            objWriter.writeShort(String2Short(ins));
                        }
                        else
                            ErrorMessage2.addLast("Unrecognized opcode");
                    }
                    else {
                        switch (opcode) {
                            case 16:
                                pc++;
                                if (Label.containsKey(clauseBlock[1])) {
                                    ins = Int2BinStr(Label.get(clauseBlock[1]), 16, false);
                                }
                                else {
                                    ins = Int2BinStr(clauseBlock[1], 16, false);
                                    if (ins == null)
                                        ins = Int2BinStr(clauseBlock[1], 16, true);
                                }
                                binWriter.write(ins);
                                binWriter.newLine();
                                objWriter.writeShort(String2Short(ins));
                                break;
                            case 17:
                                ins = "0000000000000000";
                                for (int i = 0; i < String2Int(clauseBlock[1]); i++) {
                                    pc++;
                                    binWriter.write(ins);
                                    binWriter.newLine();
                                    objWriter.writeShort(String2Short(ins));
                                }
                                break;
                            case 18:
                                for (int i = 1; i < clauseBlock[1].length() - 1; i++) {
                                    pc++;
                                    ins = Int2BinStr(clauseBlock[1].charAt(i), 16, false);
                                    binWriter.write(ins);
                                    binWriter.newLine();
                                    objWriter.writeShort(String2Short(ins));
                                }
                                pc++;
                                ins = "0000000000000000";
                                binWriter.write(ins);
                                binWriter.newLine();
                                objWriter.writeShort(String2Short(ins));
                                break;
                        }
                    }
                }
                else {
                    ErrorMessage2.addLast("Unrecognized opcode");
                }
            }
            binWriter.write("---By EnsZhou---");
            binWriter.close();
            objWriter.close();
            binFileWriter.close();
            objStream.close();
            System.out.println("Pass 2 is OK \nThe true number of instructions is " + (pc - origin));
        } catch (Exception e) {
            if (pass == 0) {
                for (String str : ErrorMessage1) {
                    System.out.println(str);
                }
                System.out.println("Line " + (rowIndex - origin) + ": the error clause is: \"" + clause + "\"");

            }
            else {
                for (String str : ErrorMessage2) {
                    System.out.println(str);
                }
                System.out.println("Line " + (pc - origin + 1) + ": the error clause is: \"" + clause + "\"");
            }
            e.printStackTrace();
        }

    }


    private static short String2Short(String string) {
        int length, Max = 1;
        int sum = 0;
        char[] str;
        str = string.toCharArray();
        length = str.length;
        for (int i = 0; i < length; i++) {
            sum *= 2;
            Max *= 2;
            sum += str[i] - '0';
        }
        if (str[0] == '1')
            return (short) (sum - Max);
        else
            return (short) sum;
    }

    private static int String2Int(String string) {
        char[] str = string.toUpperCase().toCharArray();
        int sum = 0;
        switch (str[0]) {
            case 'X':
                if (str[1] == '-') {
                    for (int i = 2; i < str.length; i++) {
                        sum *= 16;
                        if (str[i] > '9')
                            sum += str[i] - 55;
                        else
                            sum += str[i] - '0';
                    }
                    sum = -sum;
                }
                else {
                    for (int i = 1; i < str.length; i++) {
                        sum *= 16;
                        if (str[i] > '9')
                            sum += str[i] - 55;
                        else
                            sum += str[i] - '0';
                    }
                }
                break;
            case '#':
                sum = Integer.parseInt(string.substring(1));
                break;
            case 'B':
                if (str[1] == '-') {
                    for (int i = 2; i < str.length; i++) {
                        sum *= 2;
                        sum += str[i] - '0';
                    }
                    sum = -sum;
                }
                else {
                    for (int i = 1; i < str.length; i++) {
                        sum *= 2;
                        sum += str[i] - '0';
                    }
                }
                break;
        }
        return sum;
    }

    private static String Int2BinStr(int num, int bits, boolean isComponent) {
        int n, Max;
        String str = "";
        Max = (int) Math.pow(2, bits - 1);
        if (isComponent) {
            if (num > Max - 1 || num < -Max)
                return null;
            if (num < 0) {
                num += 2 * Max;
            }
            for (int i = 0; i < bits; i++) {
                n = num % 2;
                str = n + str;
                num /= 2;
            }
            return str;
        }
        else {
            if (num >= 0) {
                for (int i = 0; i < bits; i++) {
                    n = num % 2;
                    str = n + str;
                    num /= 2;
                }
                if (num > 0)
                    return null;
                else
                    return str;
            }
            else
                return null;
        }
    }

    private static String Int2BinStr(String num_str, int bits, boolean isComponent) {
        int num = String2Int(num_str);
        return Int2BinStr(num, bits, isComponent);
    }
}
