package com.dreamwalker.expandablerecyclerview.listeners;

public interface ExpandCollapseListener {

  /**
   * Called when a group is expanded
   *
   * @param positionStart the flat position of the first child in the {@link com.dreamwalker.expandablerecyclerview.models.ExpandableGroup}
   * @param itemCount the total number of children in the {@link .ExpandableGroup}
   */
  void onGroupExpanded(int positionStart, int itemCount);

  /**
   * Called when a group is collapsed
   *
   * @param positionStart the flat position of the first child in the {@link com.dreamwalker.expandablerecyclerview.models.ExpandableGroup}
   * @param itemCount the total number of children in the {@link com.dreamwalker.expandablerecyclerview.models.ExpandableGroup}
   */
  void onGroupCollapsed(int positionStart, int itemCount);
}
