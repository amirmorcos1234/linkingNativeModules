package ro.vodafone.mcare.android.ui.views.viewholders.identity;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;

/**
 * Created by Serban Radulescu on 7/17/2017.
 */

public class ExpandableListTree {

    private static final String TAG = ExpandableListTree.class.getCanonicalName();

    private Vector<Node> expandableTreeList;

    public ExpandableListTree(List<EntityChildItem> entityChildItemList) {
        expandableTreeList = new Vector<>();
       for(int i = 0 ; i < entityChildItemList.size() ; i++){
           expandableTreeList.add(new Node(entityChildItemList.get(i)));
       }
    }

    public void setExpandableTreeList(Collection<Node> expandableTreeList) {
        this.expandableTreeList = new Vector<>(expandableTreeList);
    }

    public Node getTreeItem(int position){
        if(position < 0)
            throw new IndexOutOfBoundsException();

       for(int i=0; i<expandableTreeList.size(); i++){
           //Log.d(TAG, "position in getTreeItem: " + position);
           if(position < expandableTreeList.get(i).getEntryCount()) {
               return findItemInChild(expandableTreeList.get(i), position);
           }
           else
               position -= expandableTreeList.get(i).getEntryCount();
       }

        throw new IndexOutOfBoundsException();
    }

    private Node findItemInChild(Node node, int position){
        if(position < 0)
            throw new IndexOutOfBoundsException();

        if(position == 0)
            return node;
        //passed over parent
        position -= 1;

        for(Node child : node.children){
            if(position < child.getEntryCount())
                return findItemInChild(child, position);
            else
                position -= child.getEntryCount();
        }

        //should not be reached
        throw new IndexOutOfBoundsException();
    }
    public void setNodesSelectedAfterEntityId(String entityId){

        for(Node child :expandableTreeList){
            setSelectedItemInChildWithEntitiyId(child,entityId);
        }
    }
    public void setSelectedItemInChildWithEntitiyId(Node node, String entityId){
        if(entityId ==null)
            throw new IndexOutOfBoundsException();
        if(Objects.equals(node.getItem().getEntityId(), entityId)){
            node.setSelected(true);
        }
        if(node.isExpandable()) {
            for (Node child : node.children) {
                setSelectedItemInChildWithEntitiyId(child, entityId);
            }
        }
    }

    public int getTreeSize(){
        int treeSize = 0;
        for(int i = 0 ; i < expandableTreeList.size() ; i++)
            treeSize += expandableTreeList.get(i).getEntryCount();
        return treeSize;
    }

    public void updateSelectedStates(){
        for(int i = 0 ; i < expandableTreeList.size() ; i++){
            expandableTreeList.get(i).setSelected(false);
            updateSelectedStates(  expandableTreeList.get(i));
        }
    }
    public void updateSelectedStates(Node nod){
        if(nod.isExpandable()){
            for(Node nodeChild:nod.getChildren()){
                nodeChild.setSelected(false);
                updateSelectedStates(nodeChild);
            }
        }
    }

    public static class Node {
        private EntityChildItem item;
        private Node parent;
        private String billingCustomerParent;
        private List<Node> children = new LinkedList<>();
        private boolean expanded = false;
        private int entryCount = 1;
        private boolean selected = false;

        public Node(EntityChildItem item){
            init(item);
        }

        public Node(EntityChildItem item, Node parent){
            this.parent = parent;
            init(item);
            if(expanded && parent != null)
                parent.entryCount += entryCount;
        }

        private void init(EntityChildItem item) {
            this.item = item;
            if (item.getChildList() != null) {
                if (item.getChildList().size() != 0) {
                    for (EntityChildItem child : item.getChildList()){
                        if(!child.getEntityType().equals("FinancialAccount"))
                        {
                            children.add(new Node(child, this));
                        }
                    }

                }
            }
        }

        public Node getParent() {
            return parent;
        }
        public boolean haveParent() {
            return getParent()!=null && getParent().isExpandable();
        }

        public boolean isExpandable() {
            return children.size() != 0;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public EntityChildItem getItem() {
            return item;
        }

        public void setItem(EntityChildItem item) {
            this.item = item;
        }

        public List<Node> getChildren() {
            return children;
        }

        public String getBillingCustomerParent() {
            return billingCustomerParent;
        }

        public void setBillingCustomerParent(String billingCustomerParent) {
            this.billingCustomerParent = billingCustomerParent;
        }

        public boolean isExpanded() {
            return expanded;
        }

        public void setExpanded(boolean expanded) {
            if(!isExpandable()) {
                throw new UnsupportedOperationException("Node cannot be expanded for type " + item.getEntityType());
            }
            if(this.expanded == expanded)
                return;

            int adjust = 0;

            for(Node child : children) {
                adjust += child.entryCount;
            }

            //if collapsing, subtract children count
            if(!expanded)
                adjust = -adjust;

            Node node = this;
            //update entire tree upwards
            while (node != null) {
                node.entryCount += adjust;
                node = node.parent;
            }
            this.expanded = expanded;
        }

        public int getEntryCount() {
            return entryCount;
        }
    }
}
