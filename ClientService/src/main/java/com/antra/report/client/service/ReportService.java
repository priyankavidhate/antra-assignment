package com.antra.report.client.service;

import com.antra.report.client.pojo.FileType;
import com.antra.report.client.pojo.reponse.GeneralResponse;
import com.antra.report.client.pojo.reponse.ReportVO;
import com.antra.report.client.pojo.reponse.ReportViewObj;
import com.antra.report.client.pojo.reponse.SqsResponse;
import com.antra.report.client.pojo.request.ReportRequest;

import java.io.InputStream;
import java.util.List;

import org.springframework.http.ResponseEntity;

public interface ReportService {
    ReportVO generateReportsSync(ReportRequest request);

    ReportVO generateReportsAsync(ReportRequest request);

    void updateAsyncPDFReport(SqsResponse response);

    void updateAsyncExcelReport(SqsResponse response);

   // List<ReportVO> getReportList();
    List<ReportViewObj> getReportList() ;
    InputStream getFileBodyByReqId(String reqId, FileType type);

	ResponseEntity<GeneralResponse> deleteFile(String fileId);
}
