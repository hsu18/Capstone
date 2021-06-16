function test(category, mini){
    var bigCategories = document.getElementsByClassName("menuLi");
    var miniCategories = document.getElementsByClassName("miniCategory");

    var i;
    for(i=0; i<bigCategories.length; i++){
        if(category === bigCategories[i].getElementsByTagName("a")[0].text)
            break;
    }

    bigCategories[i].getElementsByTagName("a")[0].style.fontWeight = "bold";

    miniCategories[parseInt(mini)].style.backgroundColor = "#74b4e2";
    miniCategories[parseInt(mini)].getElementsByTagName("a")[0].style.color = "white";
}

function text1() {
    let recCert = document.getElementsByClassName("userRecBar")[0];
    let recCertNames = recCert.getElementsByTagName("a");

    if (recCertNames.length == 0) {
        recCert.innerHTML = "추천 자격증이 존재하지 않습니다.";
    }

    for(const name of recCertNames){
        if(name.innerHTML.length > 6){
            name.style.top = "30%";

        }
    }
}

function textBold(find){
    var certName = document.getElementsByClassName("certTdName");
    if(certName.length == 1) return;
    for(i=1; i<certName.length; i++) {
        var regex = new RegExp(find, "g");
        certName[i].innerHTML = certName[i].innerHTML.replace(regex,
            "<span class='highlight'>" + find + "</span>");
    }
}

function textBold2(find){
    var certName = document.getElementsByClassName("certName");
    var certD = document.getElementsByClassName("certD");
    var regex = new RegExp(find, "g");
    certName[0].innerHTML = certName[0].innerHTML.replace(regex,
        "<span class='highlight'>" + find + "</span>");
    if(certD.length == 0) return;
    for(i=0; i<certD.length; i++) {
        certD[i].innerHTML = certD[i].innerHTML.replace(regex,
            "<span class='highlight'>" + find + "</span>");
    }
}