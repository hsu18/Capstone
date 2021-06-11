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