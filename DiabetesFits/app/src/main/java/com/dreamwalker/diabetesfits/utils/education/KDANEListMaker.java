package com.dreamwalker.diabetesfits.utils.education;

import android.content.Context;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.model.education.Child;
import com.dreamwalker.diabetesfits.model.education.Parent;

import java.util.Arrays;
import java.util.List;

public class KDANEListMaker {

    Context context;

    public KDANEListMaker() {
    }

    public KDANEListMaker(Context context) {
        this.context = context;
    }

    public List<Parent> makeGenres() {

        return Arrays.asList(
                makeParentOne(),
                makeParentTwo(),
                makeParentThree(),
                makeParentFour(),
                makeParentFive(),
                makeParentSix(),
                makeParentSeven(),
                makeParentEight(),
                makeParentNine());
    }

    private Parent makeParentOne() {
        return new Parent(context.getResources().getString(R.string.kdane_parent_0), makeChildOne());
    }

    private  List<Child> makeChildOne() {
        Child queen = new Child(context.getResources().getString(R.string.kdane_child_parent_00));
        Child styx = new Child(context.getResources().getString(R.string.kdane_child_parent_01));
        Child reoSpeedwagon = new Child(context.getResources().getString(R.string.kdane_child_parent_02));
        Child boston = new Child(context.getResources().getString(R.string.kdane_child_parent_03));
        Child child4 = new Child(context.getResources().getString(R.string.kdane_child_parent_04));
        return Arrays.asList(queen, styx, reoSpeedwagon, boston, child4);
    }

    private Parent makeParentTwo() {
        return new Parent(context.getResources().getString(R.string.kdane_parent_1), makeChildTwo());
    }

    private List<Child> makeChildTwo() {
        Child child0 = new Child(context.getResources().getString(R.string.kdane_child_parent_10));
        Child child1 = new Child(context.getResources().getString(R.string.kdane_child_parent_11));
        Child child2 = new Child(context.getResources().getString(R.string.kdane_child_parent_12));
        Child child3 = new Child(context.getResources().getString(R.string.kdane_child_parent_13));

        return Arrays.asList(child0, child1, child2, child3);
    }

    private Parent makeParentThree() {
        return new Parent(context.getResources().getString(R.string.kdane_parent_2), makeChildThree());
    }

    private List<Child> makeChildThree() {
        Child child0 = new Child(context.getResources().getString(R.string.kdane_child_parent_20));
        Child child1 = new Child(context.getResources().getString(R.string.kdane_child_parent_21));
        Child child2 = new Child(context.getResources().getString(R.string.kdane_child_parent_22));
        Child child3 = new Child(context.getResources().getString(R.string.kdane_child_parent_23));

        return Arrays.asList(child0, child1, child2, child3);
    }

    private Parent makeParentFour() {
        return new Parent(context.getResources().getString(R.string.kdane_parent_3), makeChildFour());
    }

    private List<Child> makeChildFour() {
        Child child0 = new Child(context.getResources().getString(R.string.kdane_child_parent_30));
        Child child1 = new Child(context.getResources().getString(R.string.kdane_child_parent_31));
        Child child2 = new Child(context.getResources().getString(R.string.kdane_child_parent_32));
        return Arrays.asList(child0, child1, child2);
    }

    private Parent makeParentFive() {
        return new Parent(context.getResources().getString(R.string.kdane_parent_4), makeChildFive());
    }

    private List<Child> makeChildFive() {
        Child child0 = new Child(context.getResources().getString(R.string.kdane_child_parent_40));
        Child child1 = new Child(context.getResources().getString(R.string.kdane_child_parent_41));
        Child child2 = new Child(context.getResources().getString(R.string.kdane_child_parent_42));
        Child child3 = new Child(context.getResources().getString(R.string.kdane_child_parent_43));
        Child child4 = new Child(context.getResources().getString(R.string.kdane_child_parent_44));
        Child child5 = new Child(context.getResources().getString(R.string.kdane_child_parent_45));

        return Arrays.asList(child0, child1, child2, child3, child4, child5);
    }

    private Parent makeParentSix() {
        return new Parent(context.getResources().getString(R.string.kdane_parent_5), makeChildSix());
    }

    private List<Child> makeChildSix() {
        Child child0 = new Child(context.getResources().getString(R.string.kdane_child_parent_50));
        Child child1 = new Child(context.getResources().getString(R.string.kdane_child_parent_51));
        Child child2 = new Child(context.getResources().getString(R.string.kdane_child_parent_52));
        Child child3 = new Child(context.getResources().getString(R.string.kdane_child_parent_53));
        Child child4 = new Child(context.getResources().getString(R.string.kdane_child_parent_54));
        Child child5 = new Child(context.getResources().getString(R.string.kdane_child_parent_55));
        Child child6 = new Child(context.getResources().getString(R.string.kdane_child_parent_56));
        return Arrays.asList(child0, child1, child2, child3, child4, child5, child6);
    }

    private Parent makeParentSeven() {
        return new Parent(context.getResources().getString(R.string.kdane_parent_6), makeChildSeven());
    }

    private List<Child> makeChildSeven() {
        Child child0 = new Child(context.getResources().getString(R.string.kdane_child_parent_60));
        Child child1 = new Child(context.getResources().getString(R.string.kdane_child_parent_61));
        Child child2 = new Child(context.getResources().getString(R.string.kdane_child_parent_62));
        Child child3 = new Child(context.getResources().getString(R.string.kdane_child_parent_63));

        return Arrays.asList(child0, child1, child2, child3);
    }

    private Parent makeParentEight() {
        return new Parent(context.getResources().getString(R.string.kdane_parent_7), makeChildEight());
    }
    private List<Child> makeChildEight() {
        Child child0 = new Child(context.getResources().getString(R.string.kdane_child_parent_70));
        Child child1 = new Child(context.getResources().getString(R.string.kdane_child_parent_71));


        return Arrays.asList(child0, child1);
    }


    private Parent makeParentNine() {
        return new Parent(context.getResources().getString(R.string.kdane_parent_8), makeChildNine());
    }
    private List<Child> makeChildNine() {
        Child child0 = new Child(context.getResources().getString(R.string.kdane_child_parent_80));
        Child child1 = new Child(context.getResources().getString(R.string.kdane_child_parent_81));
        Child child2 = new Child(context.getResources().getString(R.string.kdane_child_parent_82));
        Child child3 = new Child(context.getResources().getString(R.string.kdane_child_parent_83));

        return Arrays.asList(child0, child1, child2, child3);
    }

}
