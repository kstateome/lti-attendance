function toggleSection($val) {
    var elements = document.getElementsByClassName('hideme');

    for (var i = 0; i < elements.length; i++){
        elements[i].style.display = "none";
    }

    document.getElementById($val).style.display = "block";
}