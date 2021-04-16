function loadAll() {
    $("#report_list_body").html("");

    $.getJSON('/report',
        function (data, textStatus, jqXHR) {  // success callback
            console.info(data);
            data.data.forEach((report, index)=>{
                console.log("result report : "+report.submitter);
                console.log("result report : ",JSON.stringify(report));
                $("#report_list_body").append(
                    $('<tr>').append(
                        $('<td>').append(index + 1)
                    ).append(
                        $('<td>').append(report.submitter)
                    ).append(
                        $('<td>').append(report.description)
                    ).append(
                        $('<td>').append(report.generatedTime)
                    ).append(
                        $('<td>').append(report.pdfStatus)
                    ).append(
                        $('<td>').append(report.excelStatus)
                    ).append(
                        "<td>" + downloadLinks(report.pdfStatus, report.excelStatus, report.pdfFileId, report.excelFileId) + "</td>"
                    ).append(
                        "<td>" + actionLinks(report.pdfStatus, report.excelStatus, report.reqId, report) + "</td>"
                    )                    
                );
            });

        },function(e){
            alert('error' + e.error);
        }
    );
}

function formatTime(time) {
    if(!time){
        return "N/A";
    }
    const d = new Date(time);
    return singleDigit(d.getMonth() + 1) + '/'+singleDigit(d.getDate()) + ' ' + singleDigit(d.getHours()) + ':' + singleDigit(d.getMinutes());
}

function singleDigit(dig) {
    return ('0' + dig).slice(-2)
}

function downloadPDF(pdfFileId){
    console.log('pdfFileId :', pdfFileId)
    window.location = "https://reporting-generated-file-priyanka.s3.amazonaws.com/" + pdfFileId
}

function downloadExcel(excelFileId){
    console.log('excelFileId :', excelFileId)
    window.location = "https://reporting-generated-file-priyanka.s3.amazonaws.com/" + excelFileId
}

function downloadFile(urlToSend) {
    var req = new XMLHttpRequest();
    req.open("GET", urlToSend, true);
    req.responseType = "blob";
    req.onload = function (event) {
        console.info(event);
        if(req.status === 200) {
            var blob = req.response;
            var fileName = req.getResponseHeader("fileName")
            var link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = fileName;
            link.click();
        } else{
            alert('Error in downloading')
        }
    };
    req.send();
}
function showDelete(reqId){
    if(confirm("Are you sure to delete report?")){
        var URL = "/report/delete/"+reqId;
    $.ajax({
        url : URL,
        type: "DELETE",
        contentType: "application/json",
        dataType: "json",
        success: function(data, textStatus, jqXHR)
        {
           alert("file deleted successfully");
           loadAll();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert(jqXHR.responseJSON.message);
            console.error(jqXHR);
            console.error(jqXHR.responseJSON.message);
        }
    });
    }
}

function downloadLinks(ps, es, pdfFileId, excelFileId) {
    console.log('ps :', ps)
    console.log('es :', es)
    return (ps === 'completed'?"<a onclick='downloadPDF(\""+ pdfFileId +"\")' style='margin-left: 1em' href='#'>PDF</a>":"")
        + (es === 'completed'?"<a onclick='downloadExcel(\""+ excelFileId +"\")' style='margin-left: 1em' href='#'>Excel</a>":"")
}

function validateInput(fieldName){
    console.log('validate :', $(fieldName).val())
    try {
        return JSON.parse($(fieldName).val());
    }catch(err) {
        alert("This is not a valid Json.");
        return "";
    }
}

function update(async) {
    let reportRes = validateInput('#updateData');
    
    console.log("update data :", JSON.stringify(reportRes, null, 2))
    let reqId = reportRes.reqId;
    delete reportRes.reqId;
    if(!reportRes) {
        return false;
    }
    $.ajax({
        url : async ? "report/async/" + reqId : "report/sync/" + reqId,
        type: "PUT",
        data : JSON.stringify(reportRes),
        contentType: "application/json",
        dataType: "json",
        success: function(data, textStatus, jqXHR)
        {
            $('#update_report_model').modal('toggle');
            alert("file updated successfully");
            loadAll();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert(jqXHR.responseJSON.message);
            console.error(jqXHR);
            console.error(jqXHR.responseJSON.message);
        }
    });
}


function submit(async) {
    let data = validateInput('#inputData');
    if(!data) {
        return false;
    }
    $.ajax({
        url : async?"report/async":"report/sync",
        type: "POST",
        data : JSON.stringify(data),
        contentType: "application/json",
        dataType: "json",
        success: function(data, textStatus, jqXHR)
        {
            console.info(data);
            $('#create_report_model').modal('toggle');
            loadAll();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert(jqXHR.responseJSON.message);
            console.error(jqXHR);
            console.error(jqXHR.responseJSON.message);
        }
    });
}

function showUpdate(d) {
    console.log("starting showUpdate: ", d)
    $('#update_report_model').modal('toggle');
    var ele = document.getElementById("updateExistDataShow")
    var obj = {}
    obj.description = d.description
    obj.data = []
    console.log('d.fileData :',  d.fileData)
    for (i = 0; i < d.fileData.length; i++) {
        var temp = []
        temp.push(d.fileData[i].id)
        temp.push(d.fileData[i].name)
        temp.push(d.fileData[i].st_class)
        temp.push(d.fileData[i].score)
        obj.data.push(temp)
    }
    obj.submitter = d.submitter
    obj.reqId = d.reqId
    obj.headers = ["Student #","Name","Class","Score"]
    console.log('obj :', obj)
    ele.innerHTML = JSON.stringify(obj)
}

function actionLinks(ps, es, id, data) {
    var d = JSON.stringify(data)
    return (ps === 'completed'?"<a onclick='showDelete(\""+id+"\")' style='margin-left: 1em' href='#'>Delete</a>":"")
        + (es === 'completed'?"<a onclick='showUpdate(" + d + ")' style='margin-left: 1em' href='#'>Update</a>":"") 
}

$( document ).ready(function() {
    loadAll();
    $("#loadAllBtn").on("click",function () {
        loadAll();
    });
    $("#generateBtn").on("click",function () {
        $('#create_report_model').modal('toggle');
    });
    
    $("#create_report").on("click",function () {
        submit(false);
    });
    $("#create_report_async").on("click",function () {
        submit(true);
    });
    $("#update_report").on("click",function () {
        update(false);
    });
    $("#update_report_async").on("click",function () {
        update(true);
    });

});
