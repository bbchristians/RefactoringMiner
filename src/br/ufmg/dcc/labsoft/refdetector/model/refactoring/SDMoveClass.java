package br.ufmg.dcc.labsoft.refdetector.model.refactoring;

import gr.uom.java.xmi.diff.RefactoringType;
import br.ufmg.dcc.labsoft.refdetector.model.SDType;

public class SDMoveClass extends SDRefactoring {

    private final SDType typeBefore;
    private final SDType typeAfter;
    
    public SDMoveClass(SDType typeBefore, SDType typeAfter) {
        this(RefactoringType.MOVE_CLASS, typeBefore, typeAfter);
    }

    protected SDMoveClass(RefactoringType type, SDType typeBefore, SDType typeAfter) {
        super(type, typeAfter);
        this.typeBefore = typeBefore;
        this.typeAfter = typeAfter;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName());
        sb.append(' ');
        sb.append(typeBefore.fullName());
        sb.append(" moved to ");
        sb.append(typeAfter.fullName());
        return sb.toString();
    }
}