function enablebutton(search) {
	$(search).prop("disabled", false);
	/* $(search).addClass('ui-state-default');*/
	$(search).removeClass('ui-state-disabled'); 
}

function disablebutton(search) {
	$(search).prop("disabled", true);
	/* $(search).removeClass('ui-state-default');*/
	$(search).addClass('ui-state-disabled'); 
}