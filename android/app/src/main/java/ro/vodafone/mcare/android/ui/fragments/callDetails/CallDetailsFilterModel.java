package ro.vodafone.mcare.android.ui.fragments.callDetails;

import java.io.Serializable;

/**
 * Created by user on 10.05.2017.
 */

public class CallDetailsFilterModel implements Serializable {

    private Category category;
    private String reportType;
    private long startDate;
    private long endDate;
    private long lastBillClosedDate;
    private boolean isRoaming;
    private boolean isInternational = true;
    private boolean isNational = true;
    private String costIndicator;

    public CallDetailsFilterModel() {
    }

    public CallDetailsFilterModel(Category category, String reportType, long startDate, long endDate, long lastBillClosedDate, boolean isRoaming, boolean isInternational, boolean isNational, String costIndicator) {
        this.category = category;
        this.reportType = reportType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.lastBillClosedDate = lastBillClosedDate;
        this.isRoaming = isRoaming;
        this.isInternational = isInternational;
        this.isNational = isNational;
        this.costIndicator = costIndicator;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public long getLastBillClosedDate() {
        return lastBillClosedDate;
    }

    public void setLastBillClosedDate(long lastBillClosedDate) {
        this.lastBillClosedDate = lastBillClosedDate;
    }

    public boolean isRoaming() {
        return isRoaming;
    }

    public void setRoaming(boolean roaming) {
        isRoaming = roaming;
    }

    public boolean isInternational() {
        return isInternational;
    }

    public void setInternational(boolean international) {
        isInternational = international;
    }

    public boolean isNational() {
        return isNational;
    }

    public void setNational(boolean national) {
        isNational = national;
    }

    public String getCostIndicator() {
        return costIndicator;
    }

    public void setCostIndicator(String costIndicator) {
        this.costIndicator = costIndicator;
    }
}
