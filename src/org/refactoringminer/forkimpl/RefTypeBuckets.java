package org.refactoringminer.forkimpl;

import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import java.util.*;
import java.util.regex.Matcher;

public class RefTypeBuckets {

    // Can be Primary, Secondary, or Tertiary
    // We need to know
    //  - fileName
    //  - className
    //  - methodSig
    public Map<RefactoringType, MappingData> METHOD_REFACTORINGS = new HashMap<>();

    public static RefTypeBuckets instance = null;

    // Never Secondary
    // We need to know
    //  - fileName
    //  - className
    public static Set<RefactoringType> CLASS_REFACTORINGS = new HashSet<>(Arrays.asList(
            RefactoringType.RENAME_CLASS,
            RefactoringType.MOVE_CLASS,
            RefactoringType.MOVE_RENAME_CLASS,
            RefactoringType.EXTRACT_INTERFACE,
            RefactoringType.EXTRACT_SUPERCLASS,
            RefactoringType.EXTRACT_SUBCLASS,
            RefactoringType.EXTRACT_CLASS,
            RefactoringType.CONVERT_ANONYMOUS_CLASS_TO_TYPE,
            RefactoringType.ADD_CLASS_ANNOTATION,
            RefactoringType.REMOVE_CLASS_ANNOTATION,
            RefactoringType.MODIFY_CLASS_ANNOTATION,
            RefactoringType.INTRODUCE_POLYMORPHISM // is this the right place?
    ));

    // Contains only class variable operations
    // Never Primary
    // We need to know
    //  - fileName
    //  - className
    public static Set<RefactoringType> ATTRIBUTE_REFACTORINGS = new HashSet<>(Arrays.asList(
            RefactoringType.MOVE_ATTRIBUTE,
            RefactoringType.RENAME_ATTRIBUTE,
            RefactoringType.MOVE_RENAME_ATTRIBUTE,
            RefactoringType.REPLACE_ATTRIBUTE,
            RefactoringType.PULL_UP_ATTRIBUTE,
            RefactoringType.PUSH_DOWN_ATTRIBUTE,
            RefactoringType.EXTRACT_ATTRIBUTE,
            RefactoringType.MERGE_ATTRIBUTE,
            RefactoringType.SPLIT_ATTRIBUTE,
            RefactoringType.CHANGE_ATTRIBUTE_TYPE,
            RefactoringType.ADD_ATTRIBUTE_ANNOTATION,
            RefactoringType.REMOVE_ATTRIBUTE_ANNOTATION,
            RefactoringType.MODIFY_ATTRIBUTE_ANNOTATION
    ));

    // Never Secondary - Should this be included in Class refactorings?
    // We need to know
    //  - fileName
    public static Set<RefactoringType> FILE_REFACTORINGS = new HashSet<>(Arrays.asList(
            RefactoringType.MOVE_SOURCE_FOLDER,
            RefactoringType.RENAME_PACKAGE
    ));

    private RefTypeBuckets() {
        METHOD_REFACTORINGS.put(RefactoringType.INLINE_OPERATION, new MappingData(0, 1, 2, 2));
        METHOD_REFACTORINGS.put(RefactoringType.RENAME_METHOD, new MappingData(0, 1, 2, 2));
        METHOD_REFACTORINGS.put(RefactoringType.MOVE_OPERATION, new MappingData(0, 2, 1, 3));
        METHOD_REFACTORINGS.put(RefactoringType.MOVE_AND_INLINE_OPERATION, new MappingData(0, 3, 1, 2));
        METHOD_REFACTORINGS.put(RefactoringType.MOVE_AND_RENAME_OPERATION, new MappingData(0, 2, 1, 3));
        METHOD_REFACTORINGS.put(RefactoringType.PULL_UP_OPERATION, new MappingData(0, 2, 1, 3));
        METHOD_REFACTORINGS.put(RefactoringType.PUSH_DOWN_OPERATION, new MappingData(0, 2, 1, 3));
        METHOD_REFACTORINGS.put(RefactoringType.MERGE_OPERATION, new MappingData(0, 0, -1, -1)); // Remove? -- no class data
        METHOD_REFACTORINGS.put(RefactoringType.EXTRACT_OPERATION, new MappingData(0, 1, 2, 2));
        METHOD_REFACTORINGS.put(RefactoringType.EXTRACT_AND_MOVE_OPERATION, new MappingData(1, 1, 2, 3));
        METHOD_REFACTORINGS.put(RefactoringType.CHANGE_RETURN_TYPE, new MappingData(2, 2, 3, 3));
        METHOD_REFACTORINGS.put(RefactoringType.ADD_METHOD_ANNOTATION, new MappingData(1, 1, 2, 2));
        METHOD_REFACTORINGS.put(RefactoringType.REMOVE_METHOD_ANNOTATION, new MappingData(1, 1, 2, 2));
        METHOD_REFACTORINGS.put(RefactoringType.MODIFY_METHOD_ANNOTATION, new MappingData(2, 2, 3, 3));
        METHOD_REFACTORINGS.put(RefactoringType.EXTRACT_VARIABLE, new MappingData(1, 1, 2, 2));
        METHOD_REFACTORINGS.put(RefactoringType.INLINE_VARIABLE, new MappingData(1, 1, 2, 2));
        METHOD_REFACTORINGS.put(RefactoringType.RENAME_VARIABLE, new MappingData(2, 2, 3, 3));
        METHOD_REFACTORINGS.put(RefactoringType.RENAME_PARAMETER, new MappingData(2, 2, 3, 3));
        METHOD_REFACTORINGS.put(RefactoringType.MERGE_VARIABLE, new MappingData(2, 2, 3, 3));
        METHOD_REFACTORINGS.put(RefactoringType.SPLIT_VARIABLE, new MappingData(2, 2, 3, 3));
        METHOD_REFACTORINGS.put(RefactoringType.REPLACE_VARIABLE_WITH_ATTRIBUTE, new MappingData(2, 2, 3, 3));
        METHOD_REFACTORINGS.put(RefactoringType.PARAMETERIZE_VARIABLE, new MappingData(2, 2, 3, 3));
        METHOD_REFACTORINGS.put(RefactoringType.CHANGE_VARIABLE_TYPE, new MappingData(2, 2, 3, 3));
        METHOD_REFACTORINGS.put(RefactoringType.MERGE_PARAMETER, new MappingData(2, 2, 3, 3));
        METHOD_REFACTORINGS.put(RefactoringType.SPLIT_PARAMETER, new MappingData(2, 2, 3, 3));
        METHOD_REFACTORINGS.put(RefactoringType.CHANGE_PARAMETER_TYPE, new MappingData(2, 2, 3, 3));
        METHOD_REFACTORINGS.put(RefactoringType.ADD_PARAMETER, new MappingData(1, 1, 2, 2));
        METHOD_REFACTORINGS.put(RefactoringType.REMOVE_PARAMETER, new MappingData(1, 1, 2, 2));
        METHOD_REFACTORINGS.put(RefactoringType.REORDER_PARAMETER, new MappingData(2, 2, 3, 3));
        METHOD_REFACTORINGS.put(RefactoringType.ADD_PARAMETER_ANNOTATION, new MappingData(2, 2, 3, 3));
        METHOD_REFACTORINGS.put(RefactoringType.REMOVE_PARAMETER_ANNOTATION, new MappingData(2, 2, 3, 3));
        METHOD_REFACTORINGS.put(RefactoringType.MODIFY_PARAMETER_ANNOTATION, new MappingData(3, 3, 4, 4));
//                RefactoringType.INLINE_OPERATION,0
//                RefactoringType.RENAME_METHOD,
//                RefactoringType.MOVE_OPERATION,
//                RefactoringType.MOVE_AND_INLINE_OPERATION,
//                RefactoringType.MOVE_AND_RENAME_OPERATION,
//                RefactoringType.PULL_UP_OPERATION,
//                RefactoringType.PUSH_DOWN_OPERATION,
//                RefactoringType.MERGE_OPERATION,
//                RefactoringType.EXTRACT_OPERATION,
//                RefactoringType.EXTRACT_AND_MOVE_OPERATION,
//                RefactoringType.CHANGE_RETURN_TYPE,
//                RefactoringType.ADD_METHOD_ANNOTATION,
//                RefactoringType.REMOVE_METHOD_ANNOTATION,
//                RefactoringType.MODIFY_METHOD_ANNOTATION,
                // Variable modifications in Methods
//                RefactoringType.EXTRACT_VARIABLE,
//                RefactoringType.INLINE_VARIABLE,
//                RefactoringType.RENAME_VARIABLE,
//                RefactoringType.RENAME_PARAMETER,
//                RefactoringType.MERGE_VARIABLE,
//                RefactoringType.SPLIT_VARIABLE,
//                RefactoringType.REPLACE_VARIABLE_WITH_ATTRIBUTE,
//                RefactoringType.PARAMETERIZE_VARIABLE,
//                RefactoringType.CHANGE_VARIABLE_TYPE,
                // Parameter modifications
//                RefactoringType.MERGE_PARAMETER,
//                RefactoringType.SPLIT_PARAMETER,
//                RefactoringType.CHANGE_PARAMETER_TYPE,
//                RefactoringType.ADD_PARAMETER,
//                RefactoringType.REMOVE_PARAMETER,
//                RefactoringType.REORDER_PARAMETER,
//                RefactoringType.ADD_PARAMETER_ANNOTATION,
//                RefactoringType.REMOVE_PARAMETER_ANNOTATION,
//                RefactoringType.MODIFY_PARAMETER_ANNOTATION
    }




    public static RefTypeBuckets getInstance() {
        if( instance == null ) {
            instance = new RefTypeBuckets();
        }
        return instance;
    }

    public static MethodClassSig getMethodSignature(Refactoring methodRefactoring, RefactoringType type) {
        if( !getInstance().METHOD_REFACTORINGS.containsKey(type) ) {
            return null;
        }
        final Matcher regexMatcher = type.getRegex().matcher(normalize(methodRefactoring.toString()));
        if( regexMatcher.matches() ) {
            final MappingData typeData = getInstance().METHOD_REFACTORINGS.get(type);
            return new MethodClassSig(
                    (typeData.methodBefore == -1) ? "None" : cleanMethodSig(regexMatcher.group(typeData.methodBefore + 1)),
                    (typeData.methodAfter == -1) ? "None" : cleanMethodSig(regexMatcher.group(typeData.methodAfter + 1)),
                    (typeData.classBefore == -1) ? "None" : regexMatcher.group(typeData.classBefore + 1),
                    (typeData.classAfter == -1) ? "None" : regexMatcher.group(typeData.classAfter + 1)
            );
        }
        throw new RuntimeException("Didn't match");
    }

    public static String normalize(String refDesc) {
        return refDesc.replace("\t", " ");
    }

    public static String cleanMethodSig(String methodSig) {
        String sig = methodSig;
        // Remove any abstract or interface declarations
        sig = sig.replace("abstract", "").replace("interface", "");
        // Remove public, private, etc.
        sig = sig.substring(sig.indexOf(" ")+1, sig.length()-1).trim();
        // Remove return type if exists
        if( sig.contains(":") ) {
            sig = sig.substring(0, sig.indexOf(":")).trim();
        }
        // Remove param names if exists
            // All spaces except for the spaces separating parameters should be gone by now
        if( !sig.contains(" ") ) {
            return sig;
        }
        String[] split = sig.replace(")", "").split("\\(");
        String methodName = split[0];
        // multiple parameters
        if( sig.contains(", ") ) {
            String[] paramsSplit = split[1].split(", ");
            List<String> paramTypes = new ArrayList<>();
            for (int i = 0; i < paramsSplit.length; i++) {
                String[] paramNameTypeSplit = paramsSplit[i].split(" ");
                paramTypes.add(paramNameTypeSplit[1].trim());
            }
            return methodName + "(" + String.join(", ", paramTypes) + ")";
        }
        // Only a single parameter
        String[] paramsSplit = split[1].split(" ");
        return methodName + "(" + paramsSplit[1] + ")";
    }

    public static class MappingData {
        public int methodBefore, methodAfter, classBefore, classAfter;

        public MappingData(int mb, int ma, int c1, int c2) {
            this.methodBefore = mb;
            this.methodAfter = ma;
            this.classBefore = c1;
            this.classAfter = c2;
        }
    }

    public static class MethodClassSig {
        public String methodBefore, methodAfter, classBefore, classAfter;
        public MethodClassSig(String mb, String ma, String c1, String c2) {
            this.methodBefore = mb;
            this.methodAfter = ma;
            this.classBefore = c1;
            this.classAfter = c2;
        }
    }

}
