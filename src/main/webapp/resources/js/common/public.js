Date.prototype.format = function (format) {
    /*
     * format="yyyy-MM-dd hh:mm:ss";
     */
    var o = {
        'M+': this.getMonth() + 1,
        'd+': this.getDate(),
        'h+': this.getHours(),
        'm+': this.getMinutes(),
        's+': this.getSeconds(),
        'q+': Math.floor((this.getMonth() + 3) / 3),
        'S': this.getMilliseconds()
    };

    if (/(y+)/.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }

    for (var k in o) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
                : ("00" + o[k]).substr(("" + o[k]).length));
        }
    }
    return format;
};

var util = {
    initStars: function () {
        var self = this;
        $('.star_bg').each(function (idx, item) {
            var num = $(this).attr('data-num');
            $(this).addClass("star_" + self.getStarLevel(num));
        });
    },
    
    getStarLevel: function (starNum) {
        if ( !starNum ) {
            return 0;
        } else if ( starNum > 0 && starNum < 10 ) {
            return '01';
        } else {
            starNum += '';
            return starNum.substring(0, 1) + (starNum.substring(1, 2) == 0 ? '0' : '1');
        }
    },
    
    dayInterval: function (day, num) {
        var dayInterval;
        dayInterval = new Date(day).valueOf() + 1000 * 3600 * 24 * num;
        dayInterval = new Date(dayInterval).format("yyyy-MM-dd");
        return dayInterval;
    },

    // 获取URL中参数, 可放到工具库中
    getUrlParam: function (key) {
        var reg = new RegExp("(^|&)" + key + "=([^&]*)(&|$)", "i");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return decodeURI(r[2]);
        return null;
    },

    // 获取产品名称
    getProduct: function () {
       return null;
        
    }, 
    
    // 管理后台入口 及资源的简短名称
    simpleName: function (name) {
        return name.replace(/管理后台-|阿里应用中心|应用中心|游戏中心|个性中心|应用中心3.X|个性商店|游戏商店|应用商店|入口/g, "").replace(/轻应用{1}/, "");
    },
    
    // 同步的get请求方法
    sync_get: function (url, params) {
        var data = null;
        $.ajax({
            url: url,
            async: false,
            data: params,
            success: function (result) {
                if (result.code == 200) {
                    data = result.value;
                } else {
                    alert(result.message);
                }
            }
        });

        return data;
    },
    category: function (id, dropOne, dropTwo, id2, all) {
        // all为1或者0，1表示下拉框有全部，0表示没有
        $('.' + id).delegate('li a', 'click', function () {
            $("#" + dropOne).text($(this).text());
            var catId1 = $(this).attr('data');
            if (catId1 == undefined) {
                catId1 = ""
            }
            $("#" + dropOne).attr("data", catId1);
            $("input[name =catId1]").attr("value", catId1);
            if (!!catId1) {
                $.get("/console/category/sub_category", {"catId1": catId1}, function (result) {
                    if (result.code == 200) {
                        var data = result.value;
                        if (data == "" || data == null) {
                            $("#" + dropTwo).text("全部").attr("catId2", "");
                            $("input[name =catId2]").attr("value", "");
                        }
                        var $dropDown2 = $("." + id2);
                        $("#" + dropTwo).text("全部").attr("catId2", "");
                        $("input[name =catId2]").attr("value", "");
                        $dropDown2.html("");
                        if (all == '1') {
                            var defaultLi = "<li role='presentation'><a role='menuitem' tabindex='-1' href='javascript:void(0);' catId2=''>全部</a></li>";
                            $dropDown2.append(defaultLi);
                            $.each(data, function (i, n) {
                                var li = "<li role='presentation'><a role='menuitem' tabindex='-1' href='javascript:void(0);' catId2=" + n.id + ">" + n.name + "</a></li>";
                                $dropDown2.append(li);
                            });
                        } else {
                            $.each(data, function (i, n) {
                                var li = "<li role='presentation'><a role='menuitem' tabindex='-1' href='javascript:void(0);' catId2=" + n.id + ">" + n.name + "</a></li>";
                                $dropDown2.append(li);
                            });
                        }

                    }
                });
            } else {
                $(".search_modify_two").empty();
                $("#search_dropDown-two").attr("catid2","").text("全部");
            }
        });
        $('.' + id2).delegate('li a', 'click', function () {
            $("#" + dropTwo).text($(this).text());
            var catId2 = $(this).attr('catId2');
            $("#" + dropTwo).attr("catId2", catId2);
            $("input[name =catId2]").attr("value", catId2);
        });
    },
    paperCategory: function (id, dropTwo,id2,all) {
        // all为1或者0，1表示下拉框有全部，0表示没有
            $.get("/console/category/sub_category", {"catId1": 7}, function (result) {
                if (result.code == 200) {
                    var data = result.value;
                    if (data == "" || data == null) {
                        $("#" + dropTwo).text("全部").attr("catId2", "");
                        $("input[name =catId2]").attr("value", "");
                    }
                    var $dropDown2 = $("." + id2);
                    $("#" + dropTwo).text("全部").attr("catId2", "");
                    $("input[name =catId2]").attr("value", "");
                    $dropDown2.html("");
                    if (all == '1') {
                        var defaultLi = "<li role='presentation'><a role='menuitem' tabindex='-1' href='javascript:void(0);' catId2=''>全部</a></li>";
                        $dropDown2.append(defaultLi);
                        $.each(data, function (i, n) {
                            var li = "<li role='presentation'><a role='menuitem' tabindex='-1' href='javascript:void(0);' catId2=" + n.id + ">" + n.name + "</a></li>";
                            $dropDown2.append(li);
                        });
                    } else {
                        $.each(data, function (i, n) {
                            var li = "<li role='presentation'><a role='menuitem' tabindex='-1' href='javascript:void(0);' catId2=" + n.id + ">" + n.name + "</a></li>";
                            $dropDown2.append(li);
                        });
                    }

                }
            });
        $('.' + id2).delegate('li a', 'click', function () {
            $("#" + dropTwo).text($(this).text());
            var catId2 = $(this).attr('catId2');
            $("#" + dropTwo).attr("catId2", catId2);
            $("input[name =catId2]").attr("value", catId2);
        });
    },
    getPageId: function () {
        return $("#pageId").val();
    },
    uploadF: function (id, completeFn) {
        $("#" + id).uploadify({
            'swf': "/resources/ocean-manage/plugin/uploadify/uploadify.swf", // flash文件的相对路径
            'uploader': "/common/upload", // 后台处理程序的相对路径
            'width': '86',
            'height': '32',
            'fileObjName': '文本', // 设置上传文件名称,默认为Filedata
            'fileSizeLimit': '100MB',  // 文件大小限制
            'queueID': "icon_progress", // 文件队列的ID，该ID与存放文件队列的div的ID一致
            'fileTypeDesc': 'txt文件', // 用来设置选择文件对话框中的提示文本
            'fileTypeExts': '*.txt', // 设置可以选择的文件的类型
            'auto': true, // 设置为true当选择文件后就直接上传了，为false需要点击上传按钮才上传
            'multi': false, // 设置为true时可以上传多个文件
            'queueSizeLimit': 1,
            'buttonClass': 'btn-info',
            'buttonText': '上传', // 浏览按钮的文本，默认值：BROWSE
            'progressData': 'percentage', // 上传队列显示的数据类型，percentage是百分比，speed是上传速度
            // 回调函数
            'onUploadError': function (file, errorCode, errorMsg) {
                if (errorMsg.indexOf("500") >= 0) {
                    asyncbox.alert("上传服务器出错", "提示");
                }
            },
            'onSelectError': function (file, errorCode, errorMsg) {
                if (errorMsg.indexOf("size") >= 0) {
                    asyncbox.alert("上传的文件大小超过限制，不得超过100MB", "提示");
                } else if (errorMsg.indexOf("type") >= 0) {
                    asyncbox.alert("上传的文件类型不正确", "提示");
                }
                return false;
            },
            'onUploadSuccess': function (file, data) {

                completeFn(data);
            }
        });
    },
    uploadExcel: function (id, completeFn, fileStyle, fileName, uploadUrl, buttonText) {
        //fileStyle 要写这个格式 *.xls
        $("#" + id).uploadify({
            'swf': "/resources/ocean-manage/plugin/uploadify/uploadify.swf", //flash文件的相对路径
            'uploader': uploadUrl || "/common/upload", //后台处理程序的相对路径
            'width': '86',
            'height': '32',
            'fileObjName': fileName, //设置上传文件名称,默认为Filedata
            'fileSizeLimit': '100MB',  //文件大小限制
            'queueID': "icon_progress", //文件队列的ID，该ID与存放文件队列的div的ID一致
            'fileTypeDesc': fileName+'文件', //用来设置选择文件对话框中的提示文本
            'fileTypeExts': fileStyle, //设置可以选择的文件的类型   *.xls
            'auto': true, //设置为true当选择文件后就直接上传了，为false需要点击上传按钮才上传
            'multi': false, //设置为true时可以上传多个文件
            'queueSizeLimit': 1,
            'buttonClass': 'btn-info',
            'buttonText': buttonText || '上传', //浏览按钮的文本，默认值：BROWSE
            'progressData': 'percentage', //上传队列显示的数据类型，percentage是百分比，speed是上传速度
            //回调函数
            'onUploadError': function (file, errorCode, errorMsg) {
                if (errorMsg.indexOf("500") >= 0) {
                    asyncbox.alert("上传服务器出错", "提示");
                }
            },
            'onSelectError': function (file, errorCode, errorMsg) {
                if (errorMsg.indexOf("size") >= 0) {
                    asyncbox.alert("上传的文件大小超过限制，不得超过100MB", "提示");
                } else if (errorMsg.indexOf("type") >= 0) {
                    asyncbox.alert("上传的文件类型不正确", "提示");
                }
                return false;
            },
            'onUploadSuccess': function (file, data) {
                completeFn(data);
            }
        });
    },
    uploadFile: function (id, completeFn) {
        if (window.FormData) {
            $('#' + id).html5uploader({
                auto: true,
                multi: true,
                removeTimeout: 9999999,
                url: '/common/upload',
                fileTypeExts: '.png,.jpg,.jpeg,.gif',
                onUploadStart: function () {
                    $('.filelist').hide();
                },
                onInit: function () {
                },
                onUploadError: function () {
                },
                onUploadComplete: function (file, data) {
                    completeFn(data,id);
                }
            });
        } else {
            $("#" + id).uploadify({
                'swf': "/resources/ocean-manage/plugin/uploadify/uploadify.swf", // flash文件的相对路径
                'uploader': "/common/upload", // 后台处理程序的相对路径
                'width': '86',
                'height': '86',
                'fileObjName': '图标', // 设置上传文件名称,默认为Filedata
                'fileSizeLimit': '100MB',  // 文件大小限制
                'queueID': "icon_progress", // 文件队列的ID，该ID与存放文件队列的div的ID一致
                'fileTypeDesc': 'png文件,jpg/jpeg文件,gif文件', // 用来设置选择文件对话框中的提示文本
                'fileTypeExts': '*.png;*.jpg;*.jpeg;*.gif;', // 设置可以选择的文件的类型
                'auto': true, // 设置为true当选择文件后就直接上传了，为false需要点击上传按钮才上传
                'multi': false, // 设置为true时可以上传多个文件
                'queueSizeLimit': 1,
                'buttonImage': '/resources/ocean-manage/images/common/quick-icon.jpg',
                'buttonText': '', // 浏览按钮的文本，默认值：BROWSE
                'progressData': 'percentage', // 上传队列显示的数据类型，percentage是百分比，speed是上传速度
                // 回调函数
                'onUploadError': function (file, errorCode, errorMsg) {
                    if (errorMsg.indexOf("500") >= 0) {
                        asyncbox.alert("上传服务器出错", "提示");
                    }
                },
                'onSelectError': function (file, errorCode, errorMsg) {
                    if (errorMsg.indexOf("size") >= 0) {
                        asyncbox.alert("上传的文件大小超过限制，不得超过100MB", "提示");
                    } else if (errorMsg.indexOf("type") >= 0) {
                        asyncbox.alert("上传的文件类型不正确", "提示");
                    }
                    return false;
                },
                'onUploadSuccess': function (file, data) {
                    completeFn(data,id);
                }
            });
        }
    },
    uploadPack: function (id, completeFn, uploadUrl) {
       if (window.FormData) {
            $('#' + id).html5uploader({
                auto: true,
                multi: true,
                removeTimeout: 9999999,
                url: uploadUrl || '/apps/upload',
                fileTypeExts: '.apk,.APK',
                onUploadStart: function () {
                    $('.filelist').hide();
                },
                onInit: function () {
                },
                onUploadError: function () {
                },
                onUploadComplete: function (file, data) {
                    completeFn(data, id);
                }
            });
        } else {
            $("#" + id).uploadify({
                'swf': "/resources/ocean-manage/plugin/uploadify/uploadify.swf", // flash文件的相对路径
                'uploader': uploadUrl || "/apps/upload", // 后台处理程序的相对路径
                'width': '97',
                'height': '31',
                'fileObjName': 'apk', // 设置上传文件名称,默认为Filedata
                'fileSizeLimit': '500MB',  // 文件大小限制
                'queueID': "selectFileQueue", // 文件队列的ID，该ID与存放文件队列的div的ID一致
                'fileTypeDesc': 'apk文件,APK文件', // 用来设置选择文件对话框中的提示文本
                'fileTypeExts': '*.apk;*.APK;', // 设置可以选择的文件的类型
                'multi': false, // 设置为true时可以上传多个文件
                'queueSizeLimit': 1,
                'buttonImage': '',
                'buttonText': '浏览', // 浏览按钮的文本，默认值：BROWSE
                'progressData': 'percentage', // 上传队列显示的数据类型，percentage是百分比，speed是上传速度
                // 回调函数
                'onUploadError': function (file, errorCode, errorMsg) {
                    if (errorMsg.indexOf("500") >= 0) {
                        asyncbox.alert("上传服务器出错", "提示");
                    }
                },
                'onSelectError': function (file, errorCode, errorMsg) {
                    if (errorMsg.indexOf("size") >= 0) {
                        asyncbox.alert("上传的文件大小超过限制，不得超过500MB", "提示");
                    } else if (errorMsg.indexOf("type") >= 0) {
                        asyncbox.alert("上传的文件类型不正确", "提示");
                    }
                    return false;
                },
                'onUploadSuccess': function (file, data) {
                    completeFn(data, id);
                }
            });
        }
    },
    uploadP: function (id, completeFn) {
        if (window.FormData) {
            $('#' + id).html5uploader({
                auto: true,
                multi: true,
                removeTimeout: 9999999,
                url: '/common/upload',
                fileTypeExts: '.png',
                onUploadStart: function () {
                    $('.filelist').hide();
                },
                onInit: function () {
                },
                onUploadError: function () {
                },
                onUploadComplete: function (file, data) {
                    completeFn(data);
                }
            });
        } else {
            $("#" + id).uploadify({
                'swf': "/resources/ocean-manage/plugin/uploadify/uploadify.swf", // flash文件的相对路径
                'uploader': "/common/upload", // 后台处理程序的相对路径
                'width': '86',
                'height': '86',
                'fileObjName': '图标', // 设置上传文件名称,默认为Filedata
                'fileSizeLimit': '100MB',  // 文件大小限制
                'queueID': "icon_progress", // 文件队列的ID，该ID与存放文件队列的div的ID一致
                'fileTypeDesc': 'png文件', // 用来设置选择文件对话框中的提示文本
                'fileTypeExts': '*.png;', // 设置可以选择的文件的类型
                'auto': true, // 设置为true当选择文件后就直接上传了，为false需要点击上传按钮才上传
                'multi': false, // 设置为true时可以上传多个文件
                'queueSizeLimit': 1,
                'buttonImage': '/resources/ocean-manage/images/common/quick-icon.jpg',
                'buttonText': '', // 浏览按钮的文本，默认值：BROWSE
                'progressData': 'percentage', // 上传队列显示的数据类型，percentage是百分比，speed是上传速度
                // 回调函数
                'onUploadError': function (file, errorCode, errorMsg) {
                    if (errorMsg.indexOf("500") >= 0) {
                        asyncbox.alert("上传服务器出错", "提示");
                    }
                },
                'onSelectError': function (file, errorCode, errorMsg) {
                    if (errorMsg.indexOf("size") >= 0) {
                        asyncbox.alert("上传的文件大小超过限制，不得超过100MB", "提示");
                    } else if (errorMsg.indexOf("type") >= 0) {
                        asyncbox.alert("上传的文件类型不正确", "提示");
                    }
                    return false;
                },
                'onUploadSuccess': function (file, data) {
                    completeFn(data);
                }
            });
        }
    },
    uploadImgStyle: function (id, completeFn, extendType) {
        if (window.FormData) {
            $('#' + id).html5uploader({
                auto: true,
                multi: true,
                removeTimeout: 9999999,
                url: '/common/upload',
                fileTypeExts: '.png,.jpg' + (extendType ? ',' + extendType : ''),
                onUploadStart: function () {
                    $('.filelist').hide();
                },
                onInit: function () {
                },
                onUploadError: function () {
                },
                onUploadComplete: function (file, data) {
                    completeFn(data);
                }
            });
        } else {
            $("#" + id).uploadify({
                'swf': "/resources/ocean-manage/plugin/uploadify/uploadify.swf", // flash文件的相对路径
                'uploader': "/common/upload", // 后台处理程序的相对路径
                'width': '86',
                'height': '86',
                'fileObjName': '图标', // 设置上传文件名称,默认为Filedata
                'fileSizeLimit': '100MB',  // 文件大小限制
                'queueID': "icon_progress", // 文件队列的ID，该ID与存放文件队列的div的ID一致
                'fileTypeDesc': 'png文件,jpg文件', // 用来设置选择文件对话框中的提示文本
                'fileTypeExts': '*.png;*.jpg;' + (extendType ? '*' + extendType : ''), // 设置可以选择的文件的类型
                'auto': true, // 设置为true当选择文件后就直接上传了，为false需要点击上传按钮才上传
                'multi': false, // 设置为true时可以上传多个文件
                'queueSizeLimit': 1,
                'buttonImage': '/resources/ocean-manage/images/common/quick-icon.jpg',
                'buttonText': '', // 浏览按钮的文本，默认值：BROWSE
                'progressData': 'percentage', // 上传队列显示的数据类型，percentage是百分比，speed是上传速度
                // 回调函数
                'onUploadError': function (file, errorCode, errorMsg) {
                    if (errorMsg.indexOf("500") >= 0) {
                        asyncbox.alert("上传服务器出错", "提示");
                    }
                },
                'onSelectError': function (file, errorCode, errorMsg) {
                    if (errorMsg.indexOf("size") >= 0) {
                        asyncbox.alert("上传的文件大小超过限制，不得超过100MB", "提示");
                    } else if (errorMsg.indexOf("type") >= 0) {
                        asyncbox.alert("上传的文件类型不正确", "提示");
                    }
                    return false;
                },
                'onUploadSuccess': function (file, data) {
                    completeFn(data);
                }
            });
        }
    },
    datePickerInit: function (dom, type, compareDom, maxDate, container) {
        var years = new Date().getFullYear();
        var param = {
            dateFormat: 'yy-mm-dd',
            currentText:"今天",
            closeText: '清除',
            prevText: '前一月',
            nextText: '后一月',
            changeYear: true,
            changeMonth: true,
            yearRange: '2010:2020',
            monthNamesShort: [ '1', '2', '3', '4', '5', '6', '7', '8',
                '9', '10', '11', '12' ],
            dayNamesMin: [ '日', '一', '二', '三', '四', '五', '六' ],
            changeselect: true,
            showButtonPanel:true,
            container: container,
            onSelect: function (dateText) {
                var arys = dateText.split('-');
                $('#' + compareDom).datepicker('option', type,
                    new Date(arys[0], arys[1] - 1, arys[2]));
            },
            beforeShow : function(input, inst) {
                datepicker_CurrentInput = input;
            }
        };
        // if(maxDate) {
        //     param.maxDate = maxDate;
        // }
        $("#" + dom).datepicker(param);
        $("#ui-datepicker-div").delegate(".ui-datepicker-close","click",function(){
            datepicker_CurrentInput.value = "";
        });
    },
    
    // 去除json数据空格
    replaceBr:function (str) {
        var newStr=str.replace(/(\r\n|\n|\r)/gm, '<br>');
        return newStr;
    },
    
    // 缩略图查看
    // container--图片所在容器id，默认myModal
    // imgClass--图片class，默认scn_img
    showFullImage: function (container, imgClass) {
        container = container || "myModal";
        imgClass = imgClass || "scn_img";
        $("body").append('<div id="imgContainer" style="display: none;">'
                + '<img id="showFullImage" alt=""/></div>');
        $("#" + container).on("click", "img." + imgClass, function(e){
            var img = new Image();
            img.src = $(this).attr("src");
            img.onload = function(){
                var wid = img.width;
                var hei = img.height;
                if ( Math.min(wid, hei) > 300 ) {
                    var minW = Math.min(wid / 2.2, $(window).width());
                    var minH = Math.min(hei / 2.2, $(window).height());
                    if (minW == $(window).width()) {
                        hei = hei / (wid / minW);
                        wid = minW;
                    } else if (minH == $(window).height()) {
                        wid = wid / (hei / minH);
                        hei = minH;
                    } else {
                        wid = minW;
                        hei = minH;
                    }
                }
                
                $("#showFullImage").attr({
                    src: img.src,
                    width: wid, 
                    height: hei
                });
                
                $("#imgContainer").css({
                    "position": "absolute",
                    "z-index": 9999,
                    "left": ($(window).width() - wid)/2,
                    "top": ($(window).height() - hei)/2 + $(window).scrollTop()
                }).hide().slideDown();
            };
        });
        $("#imgContainer").on("click", "#showFullImage", function(){
            $("#imgContainer").fadeOut();
            $("#showFullImage").attr("src", "");
        });
    },
    
    hideFullImage: function () {
        $("#showFullImage").trigger("click");
    },
    
    getResolution: function (fn) {
        $.get('/console/common/basedata/get?type=RESOLUTION', function(data){
            fn && fn.call(this, data);
        });
    },
    status_list:function (id,list,text,num,flag) {
        $('.'+id).click(function () {
            var statusData = Enums.statusData;
            var $dropDown = $("."+list);
            $dropDown.empty();
            if (num == '0') {
                for(var i=0;i<statusData.length;i++){
                    var typs = statusData[i].type.split(",");
                    if ( $.inArray("application", typs) == -1 && $.inArray("all", typs) == -1) {
                        continue;
                    }
                    var li = $("<li role='presentation'><a role='menuitem' tabindex='-1' href='javascript:void(0);' value=" + statusData[i].value + ">" + statusData[i].text + "</a></li>");
                    $dropDown.append(li);
                }
            } else {
                for(var i=1;i<statusData.length;i++){
                    var typs = statusData[i].type.split(",");
                    if(statusData[i].value == '1' && $.inArray("all", typs) == -1){
                        var li_1 = $("<li role='presentation'><a role='menuitem' tabindex='-1' href='javascript:void(0);' value=" + statusData[i].value + ">" + statusData[i].text + "</a></li>");
                    }
                    if ( $.inArray("application", typs) == -1 && $.inArray("all", typs) == -1) {
                        continue;
                    }
                    var li = $("<li role='presentation'><a role='menuitem' tabindex='-1' href='javascript:void(0);' value=" + statusData[i].value + ">" + statusData[i].text + "</a></li>");
                    $dropDown.append(li);
                }
                if(flag =='1'){
                    $dropDown.append(li_1);
                }
            }

        });
        $('.'+list).delegate('li a', 'click', function () {
            $("#"+text).text($(this).text());
            var value = $(this).attr('value');
            $("#"+text).attr("value", value);
            $("input[name =status]").attr("value", value);
        });
    },
    numCheck:function (class1,id2) {
        $("."+class1).delegate("#"+id2, "keyup  paste", function () {
            $(this).val($(this).val().replace(/[^0-9]/g,''));
        }), function(){  // CTR+V事件处理
            $(this).val($(this).val().replace(/[^0-9]/g,''));
        };
    },
    initEnumSel: function () {
        $('ul.dropDown_list').each(function(){
            var _enums = Enums[$(this).attr('data-enum')];
            if ( !_enums ) {return true;}

            var opts = [];
            $(this).attr('data-sel-all')
                && opts.push("<li role='presentation'><a role='menuitem' tabindex='-1' href='javascript:void(0);' value=''>全部</a></li>");
            $.each(_enums, function(idx, item){
                opts.push("<li role='presentation'><a role='menuitem' tabindex='-1' href='javascript:void(0);' value='"
                    + item.value + "'>" + item.text + "</a></li>");
            });
            $(this).append(opts.join(''));

            $(this).on('click', 'a[role="menuitem"]', function(){
                var siblings = $(this).parent().parent().siblings();
                siblings.first().find('i').text($(this).text());
                siblings.last().val($(this).attr('value'));
            });
        });
    },
    formatNumber:function (n) {
        // 格式化金额，超过三位用逗号
        var str= n.split('').reverse().join('').replace(/(\d{3})/g,'$1,').replace(/\,$/,'').split('').reverse().join('');
        return str;
    },

    request_begin: function () {
        $('.loading_wrap').show();
    },

    request_end: function () {
        $('.loading_wrap').hide();
    },

    isInteger: function (value) {
        return typeof value === 'number' && isFinite(value) && Math.floor(value) === value;
    },

    bind: function (fn, target) {
        return function() {
            return fn.apply(target, arguments);
        }
    },

    makeDialog: function (opts) {
        var tpl = [
            '<div class="modal fade" id="myDynamicModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">',
                '<div class="modal-dialog">',
                    '<div class="modal-content">',
                        '<div class="modal-header">',
                            '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>',
                            '<h4 class="modal-title">' + (opts.title || '') + '</h4>',
                        '</div>',
                        '<div class="modal-body">',
                            opts.mainTpl || '',
                        '</div>',
                        '<div class="modal-footer">',
                            opts.footerTpl || '',
                        '</div>',
                    '</div>',
                '</div>',
            '</div>'
        ].join('');
        $('body').append(tpl);
        var $dialog = $('#myDynamicModal');
        $dialog.on('show.bs.modal', function() {
            opts.preShow && opts.preShow($dialog);
        }).on('hidden.bs.modal', function() {
            $dialog[0].innerHTML = '';
            $dialog.remove();
        });
        $dialog.modal('show');
        return $dialog;
    },

    dropDown: function (id, text, inp, fn) {
        $('.' + id).delegate('li a', 'click', function () {
            $('#' + text).text($(this).text());
            var value = $(this).data('value');
            $('input[name =' + inp + ']').val(value);
            fn && fn($(this));
        });
    },
};
var PAGE_SIZE = 20;
var oTable = {
    ooTable: null,
    dataTable: function (action, argument, insertResultFun, otherSetting) {
//        util.request_begin();
        otherSetting = otherSetting || {};
        otherSetting.tableId = otherSetting.tableId || "example";
        if (this.ooTable && otherSetting.refresh !== true) {
            $("#" + otherSetting.tableId).DataTable().draw(false);
        } else {
            pageSize = otherSetting.pageSize || PAGE_SIZE;
            isPage = otherSetting.isPage !== false;
            var json = {
            	sPaginationType: "bootstrap",
            	pagingType:   "full_numbers",
                bAutoWidth: false,
                bFilter: false,
                bDestroy: true,
                bProcessing: false,
                bPaginate: isPage,
                bServerSide: true,
                sServerMethod: "POST",
                sAjaxSource: action,
                fnServerData: function (sUrl, aoData, fnCallback, oSettings) {
                	util.request_begin();
                    var args = [{
                        name: "currentPage",
                        value: Math.ceil( oSettings._iDisplayStart / oSettings._iDisplayLength )
                    }, {
                        name: "pageSize",
                        value: pageSize
                    }];
                    args = args.concat(argument);
                    oSettings.jqXHR = $.ajax({
                        dataType: 'json',
                        url: sUrl,
                        data: args,
                        success: function (result) {
                            var currentPage= Math.ceil( oSettings._iDisplayStart / oSettings._iDisplayLength );
                            insertResultFun(result,currentPage);
                            fnCallback(result);
                            util.request_end();
                        }
                    });
                },
                createdRow: function(row, data, dataIndex) {
                    var i = 0;
                    $('td', row).each(function () {
                        this.title = $('<div>' + data[i++] + '</div>').text();
                    });
                },
                bLengthChange: false,
                bSort: false,
                iDisplayLength: pageSize,
                oLanguage: {
                    sLengthMenu: "每页显示 _MENU_ 条记录",
                    sZeroRecords: "对不起，查询不到相关数据！",
                    sEmptyTable: "表中无数据存在！",
                    sInfo: "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录",
                    sInfoFiltered: "数据表中共为 _MAX_ 条记录",
                    oPaginate: {
                        sFirst: '第一页',
                        sLast: '最后一页',
                        sNext: '下一页',
                        sPrevious: '上一页'
                    }
                }
            };
            this.ooTable = $('#' + otherSetting.tableId).dataTable(json);
        }
    },
    dataTableNum: function (action, argument, insertResultFun,num) {
        this.dataTable(action, argument, insertResultFun, {pageSize: num});
    },
    dataTableNoPage: function (action, argument, insertResultFun) {
        this.dataTable(action, argument, insertResultFun, {pageSize: null, isPage: false,refresh: true});
    }
};
