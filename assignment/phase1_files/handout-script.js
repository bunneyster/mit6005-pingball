/*
 * 6.005 Handout Script
 */

// load jQuery, run setup, load other dependencies, and render
require('https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js', function () {
  // future calls to require can use relative paths
  require.abspath = $('script[src*=handout-script]').attr('src').match(/.*\//)[0];
  
  setup();
  
  var stages = [
    [ './markdown/Markdown.Converter.js' ],
    [ './markdown/Markdown.Extra.js' ],
  ];
  (function next() {
    var scripts = stages.shift();
    if ( ! scripts) { return render(); }
    
    // require all scripts in this stage, and recurse
    $.when.apply($, scripts.map(function(script) { return require(script) })).done(next);
  })();
});

// load JavaScript by injecting a <script> tag
function require(url, callback) {
  var deferred;
  if ( ! callback) {
    // if no callback function, return a Deferred that resolves when the script is loaded
    deferred = $.Deferred();
    callback = function() { deferred.resolve(); }
  }
  
  // fix relative URLs
  url = url.replace('./', require.abspath);
  
  var script = document.createElement('script');
  script.type = 'text/javascript';
  script.src = url;
  script.onload = callback;
  document.getElementsByTagName('body')[0].appendChild(script);
  
  return deferred;
}

// perform setup that depends only on jQuery
function setup() {
  // page title
  var title = $('head title').text();
  $('.container').first().prepend($('<h1>').text(title));
  // bootstrappiness
  $('h1, markdown, .with-content').addClass('col-sm-10 col-sm-offset-1');
}

// render the page with all dependencies loaded
function render() {
  // convert all <markdown> elements
  var converter = new Markdown.Converter();
  Markdown.Extra.init(converter, {
    // note: changing the enabled extensions may break existing pages
    extensions: [ 'fenced_code_gfm', 'tables', 'def_list', 'attr_list', 'smartypants' ],
    highlighter: 'highlight',
    table_class: 'table',
  });
  $('markdown').each(function(idx, div) {
    div.innerHTML = convertMarkdown(converter, div);
    $(div).addClass('converted');
  });
  
  // syntax highlight code
  if ($('code[class^=language]').length > 0) {
    require('./highlight/highlight.pack.js', function() {
      hljs.initHighlightingOnLoad();
    });
  }
  
  // handle fragment identifiers
  if (location.hash) {
    document.getElementById(location.hash.substr(1)).scrollIntoView();
  }
}

// recursively convert a node containing Markdown and possibly HTML
function convertMarkdown(md, node) {
  return Array.prototype.map.call(node.childNodes, function(node) {
    if (node.nodeType == Node.TEXT_NODE) {
      return md.makeHtml(node.textContent);
    }
    if (node.classList.contains('no-markdown')) {
      return node.outerHTML;
    }
    return node.outerHTML.replace(node.innerHTML, convertMarkdown(md, node));
  }).join('');
}
