var utilObj = {
    mailEditor: () => {
       $('#summernote').summernote({
    placeholder: 'Please add text / content here',
    tabsize: 2,
    height: 220,
    toolbar: [

      ['font', ['bold', 'underline', 'clear']],
      ['color', ['color']],
      ['para', ['ul', 'ol', 'paragraph']],
      ['table', ['table']],
      ['style', ['style']],
      ['view', ['codeview', 'help']]
    ]
  });
   
    }}

    var mailObj = {
    setMailContent: (content) => {
       $('#summernote').summernote('code',content);
}
   
}

