var $partnersBlock, $projectPPAPartners;
var canUpdatePPAPartners, allPPAInstitutions, partnerPersonTypes, leaderType, coordinatorType, defaultType, partnerRespRequired;
var partnerOfficeRequired, managingContactsRequired, projectEditLeader, permissionLeader, permissionCoordinator;
var projectLeader;
var lWordsResp = 100;

/* A2-2440 redesign state */
var ptnText = {};
var ptnFilter = 'all';
// What the section held when the page loaded, partner by partner; the save state is the
// difference between it and the page now (see ptnSnapshot).
var ptnInitialState = null;
var ptnToastTimer = null;
var ptnUndo = null;
// The contact row created by an "Add ..." button while the users popup is open. It is
// dropped again when the popup closes without a pick.
var ptnPendingContact = null;
var ptnRoleOrder = ['PL', 'PC', 'CP'];

$(document).ready(init);

function init() {

  // Setting global variables
  $partnersBlock = $('#projectPartnersBlock');
  $projectPPAPartners = $('#projectPPAPartners');
  allPPAInstitutions = JSON.parse($('#allPPAInstitutions').val());
  canUpdatePPAPartners = ($("#canUpdatePPAPartners").val() === "true");
  partnerRespRequired = ($("#partnerRespRequired").val() === "true");
  partnerOfficeRequired = ($("#partnerOfficeRequired").val() === "true");
  managingContactsRequired = ($("#managingContactsRequired").val() === "true");
  projectEditLeader = ($("#projectEditLeader").val() === "true");
  permissionLeader = ($("#permissionLeader").val() === "true");
  permissionCoordinator = ($("#permissionCoordinator").val() === "true");
  ptnText = ptnReadText($('#ptn-i18n')[0]);
  leaderType = 'PL';
  coordinatorType = 'PC';
  defaultType = 'CP';
  partnerPersonTypes = [
      coordinatorType, leaderType, defaultType, '-1'
  ];

  if(editable) {
    // Getting the actual project leader
    projectLeader = jQuery.extend({}, getProjectLeader());
    // Remove PPA institutions from partner institution list when there is not privileges to update PPA Partners
    if(!canUpdatePPAPartners) {
      removePPAPartnersFromList('#projectPartner-template .institutionsList');
    }
  }
  // Update initial project managing partners list for each partner
  updateProjectPPAPartnersLists();

  // Activate the select2 to the existing partners
  addSelect2();

  // The users popup writes the picked person into the contact row it was opened from
  addUser = function(composedName,userId,user) {
    ptnPickUser($elementSelected.closest('.contactPerson'), composedName, userId, user || {});
    dialog.dialog("close");
  };

  // This function enables launch the pop up window
  popups();
  // Attaching listeners
  attachEvents();

  // The design's save bar carries only the save state: the shared last-edit message goes
  $('.ptn-saveBar #lastUpdateMessage').remove();

  // The help text keeps its first paragraph in view; the rest (the role descriptions some
  // global units append after a line break) waits behind "View more" with the legend.
  ptnSplitHelpText();

  $('.loadingBlock').hide();
  $('.ptn-content').fadeIn(300, function() {
    // Missing fields in parter person
    $("form .projectPartner ").each(function(i,e) {
      verifyMissingFields(e);
    });
  });

  ptnInitialState = ptnSnapshot();
  ptnRefreshAll();
  ptnRegisterCheckDetails();

  // A section with no partner opens straight on a new one
  if(editable && $('.addProjectPartner').exists() && !ptnCards().exists()) {
    ptnStartDraft();
  }

  $("textarea[id!='justification']").autoGrow();
}

function attachEvents() {

  /**
   * General
   */
  var $page = $('.ptn');

  // Expand / collapse one partner
  $page.on('click', '.projectPartner [data-ptn-toggle]', function(e) {
    e.preventDefault();
    var $card = $(this).closest('.projectPartner');
    ptnSetOpen($card, !$card.hasClass('is-open'));
    ptnRefreshExpandLabel();
  });

  // Expand / collapse every visible partner
  $page.on('click', '[data-ptn-expand-all]', function() {
    var $visible = ptnCards().filter(':visible');
    var open = $visible.filter(':not(.is-open)').length > 0;
    $visible.each(function() {
      ptnSetOpen($(this), open);
    });
    ptnRefreshExpandLabel();
  });

  // Legend behind "View more"
  $page.on('click', '[data-ptn-legend-toggle]', function() {
    var $legend = $('#ptn-legend');
    var open = $legend.is('[hidden]');
    $legend.prop('hidden', !open);
    var $extra = $('[data-ptn-note-extra]');
    $extra.prop('hidden', !open || !$.trim($extra.text()));
    $(this).attr('aria-expanded', open).text($(this).data(open ? 'labelLess' : 'labelMore'));
  });

  // Partner type filter
  $page.on('click', '[data-ptn-filter]', function() {
    ptnFilter = $(this).data('ptnFilter');
    $page.find('[data-ptn-filter]').each(function() {
      var on = $(this).data('ptnFilter') === ptnFilter;
      $(this).toggleClass('is-on', on).attr('aria-pressed', on);
    });
    ptnApplyFilters();
  });
  $page.on('click', '[data-ptn-clear-filters]', function() {
    $('#partnersSearch').val('');
    $page.find('[data-ptn-filter="all"]').trigger('click');
  });

  $('.button-save').on('click', function(e) {
    // A new partner with no organization yet holds nothing to save
    ptnCards().filter('.is-draft').each(function() {
      if(!ptnHasOrganization($(this))) {
        ptnCancelDraft($(this));
      }
    });
    var missingFields = 0
    $('form select.institutionsList').each(function(i,e){
      if(!e.value || e.value == "-1"){
        missingFields++;
      }
    });

    // Validate if there are missing fields
    if(missingFields) {
      e.preventDefault();
      var notyOptions = jQuery.extend({}, notyDefaultOptions);
      notyOptions.text = "You must select a partner organization";
      noty(notyOptions);
      // Turn off the saving button state
      turnSavingStateOff(this);
      return
    }
  });

  /**
   * Project partner Events
   */
  // Filter the partner blocks by organization name or contact person
  $('#partnersSearch').on('input', ptnApplyFilters);
  // The search box lives inside the section form, so Enter would submit (and save) the whole section
  $('#partnersSearch').on('keydown', function(e) {
    if(e.which === 13) {
      e.preventDefault();
    }
  });
  // Add a project partner Event
  $(".addProjectPartner").on('click', addPartnerEvent);
  // Remove a project partner Event
  $page.on('click', '.removePartner', removePartnerEvent);
  $page.on('click', '[data-ptn-confirm-cancel]', function() {
    ptnHideConfirm($(this).closest('.projectPartner'));
  });
  $page.on('click', '[data-ptn-confirm-ok]', function() {
    var $card = $(this).closest('.projectPartner');
    ptnHideConfirm($card);
    ptnRemovePartner($card);
  });
  // New partner (draft) actions
  $page.on('click', '[data-ptn-draft-cancel]', function() {
    ptnCancelDraft($(this).closest('.projectPartner'));
  });
  $page.on('click', '[data-ptn-draft-commit]', function() {
    ptnCommitDraft($(this).closest('.projectPartner'));
  });
  // When organization change
  $page.on("change", "select.institutionsList", function(e) {
    var $card = $(this).closest('.projectPartner');
    var partner = new PartnerObject($card);
    // Update Partner Title
    partner.updateBlockContent();

    // Get Countries from Institution ID
    $.ajax({
        url: baseURL + "/institutionBranchList.do",
        data: {
          institutionID: $(this).val(),
          phaseID: phaseID
        },
        beforeSend: function() {
          partner.startLoader();
        },
        success: function(data) {
          partner.clearCountries();

          $(partner.countriesSelect).empty();
          $(partner.countriesSelect).addOption(-1, ptnText.countryAdd || "Select a country...");

          // Validate if the current partner is not selected, then add the countries
          if(!($('input.institutionsList[value=' + partner.institutionId + ']').exists())) {
            $.each(data.branches, function(index,branch) {

              if((branch.name).indexOf("HQ") != "-1") {
                partner.addCountry({
                    iso: branch.iso,
                    name: branch.name,
                    auto: true
                });
              } else {
                $(partner.countriesSelect).addOption(branch.iso, branch.name);
              }
            });
          }
        },
        complete: function() {
          partner.stopLoader();
          ptnRefreshCard($card);
        }
    });

    // Update PPA Partners List
    updateProjectPPAPartnersLists(e);
    ptnRefreshCard($card);
  });

  // Location Elements events
  $page.on('change', '.countriesList', addLocElementCountry);
  $page.on('click', '.removeLocElement', removeLocElement);

  // Request country office
  $('#requestModal').on(
      'show.bs.modal',
      function(event) {
        $.noty.closeAll();
        var partner = new PartnerObject($(event.relatedTarget).parents('.projectPartner'));

        var $modal = $(this);
        // Show Form & button
        $modal.find('form, .requestButton').show();
        $modal.find('.messageBlock').hide();
        $modal.find('input.institution_id').val(partner.institutionId);
        $modal.find('select.countriesRequest').val(null).trigger('select2:change');
        $modal.find('select.countriesRequest').trigger('change');
        $modal.find('.modal-title').html(
            'Request Country office(s) <br /><small>(' + partner.institutionName + ')</small>');
      });
  $('#requestModal button.requestButton').on('click', function() {
    var $modal = $(this).parents('.modal');
    if($modal.find('select.countriesRequest').val() == null) {
      return

    }

    $.ajax({
        url: baseURL + '/requestCountryOffice.do',
        data: $('#requestModal form').serialize(),
        beforeSend: function(data) {
          $modal.find('.loading').fadeIn();
        },
        success: function(data) {
          if(data.sucess.result == "1") {
            // Hide Form & button
            $modal.find('form, .requestButton').hide();
            $modal.find('.messageBlock').show();
          }
        },
        complete: function() {
          $modal.find('.loading').fadeOut();
        }
    });
  });

  /**
   * Linked managing partners (toggle chips)
   */
  $page.on('click', '[data-ptn-linked-options] .ptn-toggle', function() {
    var $card = $(this).closest('.projectPartner');
    var instID = $(this).data('id');
    var $list = $card.find('.ppaPartnersList ul.list');
    var $current = $list.find('li input.id').filter(function() {
      return this.value == instID;
    }).closest('li');
    if($current.exists()) {
      $current.remove();
    } else {
      var $li = $("#ppaListTemplate").clone(true).removeAttr("id");
      $li.find('.id').val(instID);
      $li.find('.name').text($(this).attr('title') || $(this).text());
      $li.appendTo($list);
    }
    setProjectPartnersIndexes();
    updateProjectPPAPartnersLists();
    ptnRefreshCard($card);
    ptnBumpIn($card);
  });

  /**
   * Partner Person Events
   */
  // Add partner Person Event, one button per role group
  $page.on('click', '[data-ptn-add-contact]', addContactEvent);
  // Remove partner person event
  $page.on('click', '.removePerson', removePersonEvent);
  // Event when click in a relation tag of partner person
  $page.on('click', '.tag', showPersonRelations);
  // The users popup closed without a pick: the row it was opened for goes away
  $('#dialog-searchUsers').on('dialogclose', function() {
    if(ptnPendingContact) {
      var $person = ptnPendingContact.$person;
      ptnPendingContact = null;
      var $card = $person.closest('.projectPartner');
      $person.remove();
      setProjectPartnersIndexes();
      ptnRefreshCard($card);
    }
  });

  /**
   * Role tooltips
   */
  $page.on('click', '.ptn-tip__btn', function(e) {
    e.preventDefault();
    ptnToggleTip($(this).closest('.ptn-tip'));
  });
  $page.on('mouseenter focusin', '.ptn-tip', function() {
    ptnToggleTip($(this), true);
  });
  $page.on('mouseleave focusout', '.ptn-tip', function() {
    ptnToggleTip($(this), false);
  });
  $(document).on('keydown', function(e) {
    if(e.which === 27) {
      $('.ptn-tip').each(function() {
        ptnToggleTip($(this), false);
      });
    }
  });

  /**
   * Live refresh and change tracking
   */
  $page.on('input', 'textarea.resp', function() {
    ptnRefreshCard($(this).closest('.projectPartner'));
  });
  $page.on('input change', ':input[name]', function() {
    var name = $(this).attr('name');
    if(!name || this.id === 'partnersSearch' || name.indexOf('partner-') === 0) {
      return;
    }
    // A new partner is not compared until it is added to the list
    if($(this).closest('.is-draft').exists()) {
      return;
    }
    ptnRefreshSaveState();
  });
  $page.on('click', '[data-ptn-toast-undo]', function() {
    if(ptnUndo) {
      var undo = ptnUndo;
      ptnUndo = null;
      undo();
    }
    ptnHideToast();
  });

}

/* ------------------------------------------------------------------------------------------------
 * Partner cards
 * --------------------------------------------------------------------------------------------- */

/**
 * The copy the FTL hands over as data-* attributes on #ptn-i18n, keyed in camelCase.
 * Read off the attributes rather than through $.data(), which would try to parse
 * values such as "{0} people" as JSON or numbers.
 */
function ptnReadText(el) {
  var text = {};
  if(!el) {
    return text;
  }
  $.each(el.attributes, function(i,attr) {
    if(attr.name.indexOf('data-') === 0) {
      text[attr.name.slice(5).replace(/-([a-z])/g, function(m,c) {
        return c.toUpperCase();
      })] = attr.value;
    }
  });
  return text;
}

/** Moves everything after the help text's first line break into the "View more" area. */
function ptnSplitHelpText() {
  var $text = $('.ptn-note__text');
  var $extra = $('[data-ptn-note-extra]');
  var br = $text.find('br').get(0);
  if(!br || !$extra.exists()) {
    return;
  }
  var nodes = [];
  for(var n = br.nextSibling; n; n = n.nextSibling) {
    nodes.push(n);
  }
  $(br).remove();
  $extra.append(nodes);
}

/** Every partner card on the page, the template excluded. */
function ptnCards() {
  return $partnersBlock.find('.projectPartner');
}

function ptnText_(key, values) {
  var template = ptnText[key];
  if(template === undefined || template === null) {
    return '';
  }
  return String(template).replace(/\{(\d+)\}/g, function(match, i) {
    return(values && values[i] !== undefined) ? values[i] : match;
  });
}

function ptnSetOpen($card,open) {
  var $body = $card.find('> .blockContent');
  $card.toggleClass('is-open', open);
  $card.find('> .blockTitle').toggleClass('opened', open).toggleClass('closed', !open);
  $card.find('> .blockTitle .ptn-card__caret').attr('aria-expanded', open);
  if(open) {
    $body.slideDown(200, function() {
      $(this).find('textarea').autoGrow();
    });
  } else {
    $body.slideUp(200);
    ptnHideConfirm($card);
  }
  ptnRefreshToggleLabel($card);
}

function ptnRefreshToggleLabel($card) {
  var name = $.trim($card.find('.ptn-card__acr').text());
  var key = $card.hasClass('is-open') ? 'collapse' : 'expand';
  $card.find('> .blockTitle .ptn-card__caret').attr('aria-label', ptnText_(key, [
    name
  ]));
}

function ptnRefreshExpandLabel() {
  var $btn = $('[data-ptn-expand-all]');
  var $visible = ptnCards().filter(':visible');
  var allOpen = $visible.length > 0 && $visible.filter(':not(.is-open)').length === 0;
  $btn.text($btn.data(allOpen ? 'labelCollapse' : 'labelExpand'));
}

function ptnRefreshAll() {
  ptnCards().each(function() {
    ptnRefreshCard($(this));
  });
  ptnRefreshSummary();
  ptnApplyFilters();
  ptnRefreshSaveState();
}

/** The organization this card stands for, or -1 while a new partner has none. */
function ptnInstitutionId($card) {
  var value = parseInt($card.find('.institutionsList').val());
  return isNaN(value) ? -1 : value;
}

function ptnHasOrganization($card) {
  return ptnInstitutionId($card) > 0;
}

function ptnIsPPA($card) {
  return allPPAInstitutions.indexOf(ptnInstitutionId($card)) != -1;
}

function ptnWords(text) {
  var trimmed = $.trim(text || '');
  return trimmed ? trimmed.split(/\s+/).length : 0;
}

function ptnInitials(first,last) {
  return((first || '').charAt(0) + (last || '').charAt(0)).toUpperCase();
}

/** Contacts of a card, ordered leader, coordinators, collaborators and then by name. */
function ptnContacts($card) {
  var list = [];
  $card.find('.contactPerson').each(function() {
    var $p = $(this);
    if(!$p.find('input.userId').val()) {
      return;
    }
    list.push({
        $el: $p,
        type: $p.find('.partnerPersonType').val() || defaultType,
        name: $.trim($p.find('[data-ptn-person-name]').text()),
        email: $.trim($p.find('[data-ptn-person-email]').text()),
        initials: $.trim($p.find('[data-ptn-initials]').text())
    });
  });
  list.sort(function(a,b) {
    var byRole = ptnRoleOrder.indexOf(a.type) - ptnRoleOrder.indexOf(b.type);
    return byRole || a.name.localeCompare(b.name);
  });
  return list;
}

function ptnCountries($card) {
  return $card.find('.countries-list .locElement').map(function() {
    return $.trim($(this).find('.name').text());
  }).get();
}

/** Acronyms of the managing partners this card is linked through. */
function ptnLinkedNames($card) {
  return $card.find('.ppaPartnersList ul.list li input.id').map(function() {
    var id = $(this).val();
    var acronym = $.trim($('#instID-' + id + ' .acronym').text());
    return acronym || $.trim($(this).closest('li').find('.name').text());
  }).get();
}

/**
 * What the partner still misses, mirroring ProjectPartnersValidator rule for rule, each
 * gated by the same specificity: the chip must never claim more (or less) than the
 * server will flag when the section is checked.
 */
function ptnIssues($card) {
  var issues = [];
  var isPPA = ptnIsPPA($card);
  if(!ptnHasOrganization($card)) {
    issues.push(ptnText.issueOrg);
  }
  if(managingContactsRequired && projectEditLeader && partnerRespRequired) {
    var resp = $card.find('textarea.resp, input[name$=".responsibilities"]').first().val();
    if(!$.trim(resp)) {
      issues.push(ptnText.issueResp);
    } else if(ptnWords(resp) > lWordsResp) {
      issues.push(ptnText.issueRespLong);
    }
  }
  if(partnerOfficeRequired && !ptnCountries($card).length) {
    issues.push(ptnText.issueCountry);
  }
  if(managingContactsRequired && projectEditLeader && !isPPA && !ptnLinkedNames($card).length) {
    issues.push(ptnText.issueLinked);
  }
  if(managingContactsRequired && isPPA && !ptnContacts($card).length) {
    issues.push(ptnText.issueContact);
  }
  return issues;
}

/** Why this partner cannot be removed, or '' when it can. Same rules as before the redesign. */
function ptnRemoveBlockReason($card) {
  var partner = new PartnerObject($card);
  if(partner.hasLeader()) {
    return ptnText.removeBlockedLeader;
  }
  var isPPA = ptnIsPPA($card);
  if(isPPA) {
    var linked = partner.hasPartnerContributions();
    if(linked.length) {
      return ptnText_('removeBlockedLinked', [
        linked.join(', ')
      ]);
    }
    if(!canUpdatePPAPartners) {
      return ptnText.removeBlockedPpa;
    }
  }
  var activities = partner.getRelationsNumber('activities');
  if(activities > 0) {
    return ptnText_('removeBlockedActivities', [
      activities
    ]);
  }
  return '';
}

/** Redraws everything on a card that is derived from its fields. */
function ptnRefreshCard($card) {
  if(!$card || !$card.exists()) {
    return;
  }
  var isPPA = ptnIsPPA($card);
  var instID = ptnInstitutionId($card);
  var contacts = ptnContacts($card);
  var countries = ptnCountries($card);
  var linked = ptnLinkedNames($card);

  // Title: acronym and full name. Saved partners are rendered by the server; a new one
  // takes them from the hidden institutions list.
  if(instID > 0 && $('#instID-' + instID).exists()) {
    var acronym = $.trim($('#instID-' + instID + ' .acronym').text());
    var name = $.trim($('#instID-' + instID + ' .name').text());
    if(!acronym) {
      acronym = name;
      name = '';
    }
    $card.find('.ptn-card__acr').text(acronym);
    $card.find('.ptn-card__name').text(name ? '— ' + name : '').attr('title', name);
  }
  $card.toggleClass('is-ppa', isPPA).toggleClass('is-orgless', instID <= 0);

  // Meta line
  $card.find('[data-ptn-countries-text]').text(countries.length ? countries.join(', ') : ptnText.noCountry);
  var $avatars = $card.find('[data-ptn-avatars]').empty();
  $.each(contacts.slice(0, 4), function(i,c) {
    $('<span class="ptn-avatar ptn-avatar--xs"></span>').addClass('ptn-avatar--' + c.type).text(c.initials).appendTo(
        $avatars);
  });
  var leader = contacts.filter(function(c) {
    return c.type == leaderType;
  })[0];
  var contactsLine = !contacts.length ? ptnText.noContacts : (contacts.length == 1 ? ptnText.contactsOne : ptnText_(
      'contactsOther', [
        contacts.length
      ]));
  if(leader) {
    contactsLine += ' · ' + ptnText_('contactsLeader', [
      leader.name
    ]);
  }
  $card.find('[data-ptn-contacts-text]').text(contactsLine);
  var showLinked = !isPPA && linked.length > 0;
  $card.find('[data-ptn-linked]').prop('hidden', !showLinked);
  $card.find('[data-ptn-linked-text]').text(ptnText_('linkedVia', [
    linked.join(', ')
  ]));
  $card.find('[data-ptn-haystack]').text($.map(contacts, function(c) {
    return c.name + ' ' + c.email;
  }).join(' '));

  // Partner type tag
  $card.find('[data-ptn-type]').text(isPPA ? ptnText.tagManaging : ptnText.tagPartner).toggleClass(
      'ptn-tag--managing', isPPA).toggleClass('ptn-tag--partner', !isPPA);

  // The leader cannot be removed (replace the leader first): its remove button says so
  $card.find('.contactPerson').each(function() {
    var isLeader = $(this).find('.partnerPersonType').val() == leaderType;
    $(this).find('.removePerson').toggleClass('is-disabled', isLeader).attr('aria-disabled', isLeader ? 'true' : 'false')
        .attr('title', isLeader ? ptnText.personRemoveBlockedLeader : $(this).find('.removePerson').attr('aria-label'));
  });

  // What is still missing; the relations (OICRs, deliverables...) take the slot when nothing is
  var issues = $card.hasClass('is-draft') ? [] : ptnIssues($card);
  var $issues = $card.find('[data-ptn-issues]');
  $issues.prop('hidden', !issues.length).text(ptnText_('missing', [
    issues.length
  ])).attr('title', ptnText_('missingTitle', [
    issues.join(', ')
  ]));
  $card.find('[data-ptn-relations]').prop('hidden', issues.length > 0);
  $card.toggleClass('has-issues', issues.length > 0);

  // Remove button: disabled, with the reason, when a rule blocks it
  var reason = ptnRemoveBlockReason($card);
  var acronymText = $.trim($card.find('.ptn-card__acr').text());
  $card.find('.removePartner').attr('aria-disabled', reason ? 'true' : 'false').toggleClass('is-disabled', !!reason)
      .attr('title', reason || ptnText_('remove', [
        acronymText
      ]));

  // Responsibilities: word counter and its error
  var $resp = $card.find('textarea.resp');
  if($resp.exists()) {
    var words = ptnWords($resp.val());
    var over = words > lWordsResp;
    var tried = $card.hasClass('is-tried');
    var empty = tried && partnerRespRequired && !$.trim($resp.val());
    $card.find('[data-ptn-resp-counter]').text(ptnText_('respCounter', [
      lWordsResp - words
    ])).toggleClass('is-over', over);
    $card.find('[data-ptn-resp-error]').text(over ? ptnText.respOver : (empty ? ptnText.respRequired : ''));
    $resp.toggleClass('is-invalid', over || empty);
  }
  // "+ Add country" only while the organization still has an office to add
  var $countrySelect = $card.find('select.countriesList');
  var officesLeft = $countrySelect.find('option').filter(function() {
    return this.value && this.value != '-1';
  }).length;
  $card.find('.ptn-chips__add').prop('hidden', !officesLeft);
  $card.find('.countries-list').toggleClass('is-invalid', $card.hasClass('is-tried') && partnerOfficeRequired &&
      !countries.length);

  // Linked managing partners: note under the chips
  var linkedMissing = $card.hasClass('is-tried') && !isPPA && !linked.length;
  var hasOptions = $card.find('[data-ptn-linked-options] .ptn-toggle').length > 0;
  $card.find('[data-ptn-linked-note]').text(
      linkedMissing ? ptnText.linkedMissing : (hasOptions ? ptnText.linkedNote : ptnText.linkedNone)).toggleClass(
      'is-error', linkedMissing);
  $card.find('[data-ptn-linked-options]').toggleClass('is-invalid', linkedMissing);
  // Hidden for managing partners, and on a new partner until its organization is known
  $card.find('.ppaPartnersList').toggle(!isPPA && instID > 0);

  // Contacts: per-role counts, empty states and the add / replace leader buttons
  // Read the live value: .val() changes the property, never the value attribute a
  // [value="PL"] selector would match.
  var clusterHasLeader = ptnCards().find('.contactPerson .partnerPersonType').filter(function() {
    return this.value == leaderType && $(this).closest('.contactPerson').find('input.userId').val();
  }).length > 0;
  $card.find('[data-ptn-group]').each(function() {
    var role = $(this).data('ptnGroup');
    var count = $(this).find('.contactPerson').filter(function() {
      return $(this).find('input.userId').val();
    }).length;
    $(this).find('[data-ptn-group-count]').text(count);
    $(this).find('[data-ptn-group-empty]').prop('hidden', count > 0);
    if(role == leaderType) {
      var $add = $(this).find('[data-ptn-add-contact]');
      $add.find('[data-ptn-add-label]').text(clusterHasLeader ? ptnText.replaceLeader : ptnText.addLeader);
      // Said through aria-describedby, not a native title: a title tooltip stays on screen
      // after the users popup it opened has closed.
      $add.attr('aria-label', clusterHasLeader ? ptnText.replaceLeader + '. ' + ptnText.replaceLeaderTitle : null);
    }
  });
  $card.find('[data-ptn-people]').text(!contacts.length ? '' : (contacts.length == 1 ? ptnText.peopleOne : ptnText_(
      'peopleOther', [
        contacts.length
      ])));
  $card.find('[data-ptn-no-contacts]').prop('hidden', contacts.length > 0 || !isPPA);
  $card.find('.contactsPerson .requiredTag').toggle(isPPA);

  // Draft footer note
  if($card.hasClass('is-draft')) {
    ptnRefreshDraftNote($card);
  }
  ptnRefreshToggleLabel($card);
}

/** Header summary line and the filter counts. */
function ptnRefreshSummary() {
  var $cards = ptnCards().not('.is-draft');
  var managing = $cards.filter('.is-ppa').length;
  var people = 0;
  $cards.each(function() {
    people += ptnContacts($(this)).length;
  });
  $('[data-ptn-summary]').text(ptnText_('summary', [
      $cards.length, managing, people
  ]));
  $('[data-ptn-count="all"]').text($cards.length);
  $('[data-ptn-count="mp"]').text(managing);
  $('[data-ptn-count="partner"]').text($cards.length - managing);
  $('.ptn-empty--none').css('display', ptnCards().exists() ? 'none' : 'flex');
}

/**
 * Shows the cards that match the search and the partner-type filter. Only visibility
 * changes: a hidden partner's inputs are still part of the form and are still posted,
 * so a filtered list saves exactly like an unfiltered one.
 */
function ptnApplyFilters() {
  var term = ($('#partnersSearch').val() || '').toLowerCase().trim();
  var shown = 0;
  ptnCards().each(function() {
    var $card = $(this);
    var $match = $card.find('[data-ptn-match]');
    if($card.hasClass('is-draft')) {
      $card.show();
      $match.prop('hidden', true);
      return;
    }
    var title = ($card.find('.ptn-card__acr').text() + ' ' + $card.find('.ptn-card__name').text()).toLowerCase();
    var matchedPeople = [];
    if(term && title.indexOf(term) === -1) {
      $.each(ptnContacts($card), function(i,c) {
        if((c.name + ' ' + c.email).toLowerCase().indexOf(term) !== -1) {
          matchedPeople.push(c.name);
        }
      });
    }
    var matchesTerm = !term || title.indexOf(term) !== -1 || matchedPeople.length > 0;
    var isPPA = $card.hasClass('is-ppa');
    var matchesType = ptnFilter === 'all' || (ptnFilter === 'mp' && isPPA) || (ptnFilter === 'partner' && !isPPA);
    var visible = matchesTerm && matchesType;
    $card.toggle(visible);
    $match.prop('hidden', !matchedPeople.length).text(ptnText_('match', [
      matchedPeople.join(', ')
    ]));
    if(visible) {
      shown++;
    }
  });
  $('.partnersSearch-empty').css('display', (ptnCards().exists() && !shown) ? 'flex' : 'none');
  ptnRefreshExpandLabel();
}

/* ------------------------------------------------------------------------------------------------
 * Change tracking, toast and undo
 * --------------------------------------------------------------------------------------------- */

/**
 * The section's saved content, keyed by partner so that re-indexing after an add or a
 * removal does not read as a change: responsibilities, sub-department, country offices,
 * linked managing partners, contacts with their role, and divisions. New partners are
 * left out while they are still a draft.
 */
function ptnSnapshot() {
  var state = {};
  var sorted = function($els,fn) {
    return $els.map(fn).get().sort().join('|');
  };
  ptnCards().not('.is-draft').each(function() {
    var $card = $(this);
    var id = $card.find('.partnerId').val();
    var key = id ? 'id-' + id : 'inst-' + ptnInstitutionId($card);
    state[key] = {
        resp: $.trim($card.find('[name$=".responsibilities"]').val() || ''),
        sub: $.trim($card.find('input[name$=".subDepartment"]').val() || ''),
        countries: sorted($card.find('.locElement input.locElementCountry'), function() {
          return this.value;
        }),
        linked: sorted($card.find('.ppaPartnersList ul.list li input.id'), function() {
          return this.value;
        }),
        contacts: sorted($card.find('.contactPerson'), function() {
          var userId = $(this).find('input.userId').val();
          return userId ? userId + ':' + $(this).find('.partnerPersonType').val() : null;
        }),
        divisions: sorted($card.find('.contactPerson select.divisionField'), function() {
          return $(this).closest('.contactPerson').find('input.userId').val() + ':' + this.value;
        })
    };
  });
  return state;
}

/** How many changes the page holds against what it loaded with. */
function ptnCountChanges() {
  if(!ptnInitialState) {
    return 0;
  }
  var current = ptnSnapshot();
  var count = 0;
  var keys = {};
  $.each(ptnInitialState, function(k) {
    keys[k] = true;
  });
  $.each(current, function(k) {
    keys[k] = true;
  });
  $.each(keys, function(k) {
    var before = ptnInitialState[k];
    var now = current[k];
    if(!before || !now) {
      // A partner added or removed is one change, whatever it holds
      count++;
      return;
    }
    $.each(before, function(field,value) {
      if(now[field] !== value) {
        count++;
      }
    });
  });
  return count;
}

/** Kept as the hook every edit calls; the count itself is always recomputed. */
function ptnBumpIn($el) {
  ptnRefreshSaveState();
}

function ptnBump() {
  ptnRefreshSaveState();
}

function ptnRefreshSaveState() {
  var count = ptnCountChanges();
  var $state = $('[data-ptn-save-state]');
  $state.toggleClass('is-dirty', count > 0);
  $state.find('[data-ptn-save-text]').text(!count ? ptnText.saveClean : (count == 1 ? ptnText.unsavedOne : ptnText_(
      'unsavedOther', [
        count
      ])));
}

function ptnShowToast(text,undo) {
  var $toast = $('[data-ptn-toast]');
  clearTimeout(ptnToastTimer);
  ptnUndo = undo || null;
  $toast.find('[data-ptn-toast-text]').text(text);
  $toast.find('[data-ptn-toast-undo]').prop('hidden', !undo);
  $toast.prop('hidden', false);
  ptnToastTimer = setTimeout(ptnHideToast, 7000);
}

function ptnHideToast() {
  clearTimeout(ptnToastTimer);
  ptnUndo = null;
  $('[data-ptn-toast]').prop('hidden', true);
}

/* ------------------------------------------------------------------------------------------------
 * Role tooltips
 * --------------------------------------------------------------------------------------------- */

function ptnToggleTip($tip,show) {
  var $body = $tip.find('.ptn-tip__body');
  var open = (show === undefined) ? $body.is('[hidden]') : show;
  $body.prop('hidden', !open);
  $tip.find('.ptn-tip__btn').attr('aria-expanded', open);
}

/* ------------------------------------------------------------------------------------------------
 * "Check for missing fields" in the sidebar: the Partners row lists each partner
 * --------------------------------------------------------------------------------------------- */

function ptnRegisterCheckDetails() {
  if(typeof clusterMenuSectionDetails === 'undefined') {
    return;
  }
  clusterMenuSectionDetails.partners = function() {
    var rows = [];
    var section = $.trim($('#menu-partners > a').text());
    ptnCards().not('.is-draft').each(function() {
      var $card = $(this);
      var issues = ptnIssues($card);
      if(!issues.length) {
        return;
      }
      $card.addClass('is-tried');
      ptnRefreshCard($card);
      rows.push({
          title: section + ' · ' + $.trim($card.find('.ptn-card__acr').text()),
          detail: ptnText_('checkMissing', [
            issues.join(', ')
          ]),
          onPick: function() {
            $('#partnersSearch').val('');
            $('[data-ptn-filter="all"]').trigger('click');
            ptnSetOpen($card, true);
            $('html, body').animate({
              scrollTop: $card.offset().top - 90
            }, 400);
          }
      });
    });
    return rows;
  };
}

function getProjectLeader() {
  var contactLeader = {};
  $partnersBlock.find('.contactPerson').each(function(i,partnerPerson) {
    var contact = new PartnerPersonObject($(partnerPerson));
    if(contact.isLeader()) {
      contactLeader = jQuery.extend({}, contact);
    }
  });
  return contactLeader;
}

function setProjectLeader(obj) {
  projectLeader = jQuery.extend({}, obj);
}

function showPersonRelations(e) {
  var $relations = $(this).next().html();
  $('#relations-dialog').dialog({
      modal: true,
      closeText: "",
      width: 500,
      buttons: {
        Close: function() {
          $(this).dialog("close");
        }
      },
      open: function() {
        $(this).html($relations);
      },
      close: function() {
        $(this).empty();
      }
  });
}

/** Moves a contact card under the heading of its role. */
function ptnPlaceInGroup($person,type) {
  var $group = $person.closest('.projectPartner').find('[data-ptn-group="' + type + '"] .ptn-group__members');
  if($group.exists() && !$person.parent().is($group)) {
    $group.append($person);
  }
}

function removePPAPartnersFromList(list) {
  for(var i = 0, len = allPPAInstitutions.length; i < len; i++) {
    $(list).find('option[value=' + allPPAInstitutions[i] + ']').remove();
  }
  $(list).trigger("change.select2");
}

/**
 * Refreshes the project's managing partners and, for every other partner, the toggle chips
 * used to say which of them it is linked through.
 */
function updateProjectPPAPartnersLists(e) {
  var projectInstitutions = [];
  // Clean PPA partners from hidden select
  $projectPPAPartners.empty();
  // Loop for all projects partners
  $partnersBlock.find('.projectPartner').each(function(i,projectPartner) {
    var partner = new PartnerObject($(projectPartner));
    // Collecting partners institutions
    projectInstitutions.push(parseInt(partner.institutionId));
    // Validating if the partners is PPA Partner
    if(partner.isPPA()) {
      partner.hidePPAs();
      // Collecting the managing partners of the project
      $projectPPAPartners.append(setOption(partner.institutionId, partner.institutionName));
    } else {
      if(partner.institutionId == -1) {
        partner.hidePPAs();
      } else {
        partner.showPPAs();
      }
    }
  });

  // Validating if the institution chosen is already selected
  if(e) {
    var $fieldError = $(e.target).parents('.partnerName').find('p.fieldErrorInstitutions');
    $fieldError.text('');
    var count = 0;
    // Verify if the partner is already selected
    for(var i = 0; i < projectInstitutions.length; ++i) {
      if(projectInstitutions[i] == e.target.value) {
        count++;
      }
    }
    // If there is one selected , show an error message
    if(count > 1) {
      var institutionName = $(e.target).find('option[value="' + e.target.value + '"]').text();
      var institutionName_saved =
          $('input.institutionsList[value=' + e.target.value + ']').parents('.projectPartner').find('.partnerTitle')
              .text();
      $fieldError.text('"' + (institutionName || institutionName_saved) + '" is already selected').animateCss(
          'flipInX');
      $(e.target).val(null).trigger('change.select2');
    }
  }

  // Drawing the linked managing partner chips for each partner
  $partnersBlock.find('.projectPartner').each(function(i,partner) {
    var $options = $(partner).find('[data-ptn-linked-options]');
    if(!$options.exists()) {
      return;
    }
    var selected = $(partner).find('.ppaPartnersList ul.list li input.id').map(function() {
      return $(this).val();
    }).get();
    var seen = {};
    $options.empty();
    var addChip = function(id,name) {
      if(seen[id]) {
        return;
      }
      seen[id] = true;
      var on = selected.indexOf(String(id)) != -1;
      var acronym = $.trim($('#instID-' + id + ' .acronym').text()) || name;
      var $chip = $('<button type="button" class="ptn-toggle"></button>').attr({
          'data-id': id,
          'aria-pressed': on,
          title: name
      }).toggleClass('is-on', on).prop('disabled', !editable);
      $('<span class="ptn-toggle__box" aria-hidden="true"></span>').html(
          '<svg width="8" height="8" viewBox="0 0 10 10" fill="none"><path d="M2 5.2 4.1 7.2 8 3" stroke="#fff" '
              + 'stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>').appendTo($chip);
      $('<span></span>').text(acronym).appendTo($chip);
      if(editable || on) {
        $chip.appendTo($options);
      }
    };
    $projectPPAPartners.find('option').each(function() {
      addChip($(this).val(), $(this).text());
    });
    // A link to a managing partner that is no longer in the section stays visible, so it
    // can still be removed.
    $(partner).find('.ppaPartnersList ul.list li').each(function() {
      addChip($(this).find('input.id').val(), $.trim($(this).find('.name').text()));
    });
  });
}

/** Turns every contact of the given type into a collaborator: there is one leader per project. */
function setPartnerTypeToDefault(type,$except) {
  $partnersBlock.find('.projectPartner').each(function(i,partner) {
    var projectPartner = new PartnerObject($(partner));
    $(partner).find('.contactPerson').each(function(i,partnerPerson) {
      if($except && $(partnerPerson).is($except)) {
        return;
      }
      var contact = new PartnerPersonObject($(partnerPerson));
      if(contact.type == type) {
        $(partnerPerson).removeClass(partnerPersonTypes.join(' ')).addClass(defaultType);
        contact.setPartnerType(defaultType);
        ptnPlaceInGroup($(partnerPerson), defaultType);
      }
    });
    projectPartner.changeType();
  });
}

function removePartnerEvent(e) {
  e.preventDefault();
  var $card = $(this).closest('.projectPartner');
  var reason = ptnRemoveBlockReason($card);
  if(reason) {
    var notyOptions = jQuery.extend({}, notyDefaultOptions);
    notyOptions.text = reason;
    noty(notyOptions);
    return;
  }
  var partner = new PartnerObject($card);
  var acronym = $.trim($card.find('.ptn-card__acr').text()) || partner.institutionName;
  var text = ptnText_('removeConfirm', [
    acronym
  ]);
  var contacts = ptnContacts($card).length;
  if(contacts) {
    text += ' ' + ptnText_('removeConfirmContacts', [
      contacts
    ]);
  }
  var deliverables = partner.getRelationsNumber('deliverables');
  if(deliverables > 0) {
    text += ' ' + ptnText_('removeConfirmDeliverables', [
      deliverables
    ]);
  }
  var $confirm = $card.find('> .ptn-confirm');
  $confirm.find('[data-ptn-confirm-text]').text(text);
  $confirm.prop('hidden', false);
  $confirm.find('[data-ptn-confirm-cancel]').trigger('focus');
}

function ptnHideConfirm($card) {
  $card.find('> .ptn-confirm').prop('hidden', true);
}

/** Takes a partner out of the form, with an undo while the toast is up. */
function ptnRemovePartner($card) {
  var $prev = $card.prev();
  var acronym = $.trim($card.find('.ptn-card__acr').text());
  $card.detach();
  updateProjectPPAPartnersLists();
  setProjectPartnersIndexes();
  ptnRefreshAll();
  ptnBump();
  ptnShowToast(ptnText_('removed', [
    acronym
  ]), function() {
    if($prev.exists() && $prev.parent().exists()) {
      $prev.after($card);
    } else {
      $partnersBlock.prepend($card);
    }
    updateProjectPPAPartnersLists();
    setProjectPartnersIndexes();
    ptnRefreshAll();
    ptnBump();
  });
}

function addPartnerEvent(e) {
  e.preventDefault();
  ptnStartDraft();
}

/* ------------------------------------------------------------------------------------------------
 * New partner (draft card)
 * --------------------------------------------------------------------------------------------- */

function ptnStartDraft() {
  if(ptnCards().filter('.is-draft').exists()) {
    return;
  }
  // The draft always shows, so the filters step aside for it
  $('#partnersSearch').val('');
  $('[data-ptn-filter="all"]').trigger('click');

  var $newElement = $("#projectPartner-template").clone(true).removeAttr("id");
  var draftId = 'draft-' + new Date().getTime();
  $newElement.addClass('is-draft is-open is-orgless');
  $newElement.find('#ptn-body-template').attr('id', 'ptn-body-' + draftId);
  $newElement.find('.ptn-card__caret').attr('aria-controls', 'ptn-body-' + draftId);
  $newElement.find('textarea.resp').attr('id', 'resp-' + draftId);
  $newElement.find('label[for="resp-template"]').attr('for', 'resp-' + draftId);
  $partnersBlock.append($newElement);
  $newElement.find('> .blockContent').show();
  $newElement.show();
  $('.addProjectPartner').prop('disabled', true);
  $(document).trigger('updateComponent');

  // Activate the select2 plugin for new partners created
  // Organization
  $newElement.find("select.institutionsList").select2(searchInstitutionsOptions(canUpdatePPAPartners));
  $newElement.find("select.institutionsList").parent().find("span.select2-selection__placeholder").text(
      ptnText.orgPlaceholder || placeholderText);

  // Other Selects
  $newElement.find('select.setSelect2').select2({
      width: '100%'
  });

  // Update indexes
  setProjectPartnersIndexes();
  updateProjectPPAPartnersLists();
  ptnRefreshCard($newElement);
  ptnRefreshSummary();
  ptnApplyFilters();

  $('html, body').animate({
    scrollTop: $newElement.offset().top - 90
  }, 300, function() {
    $newElement.find("select.institutionsList").select2('open');
  });
}

function ptnDraftMissing($card) {
  var missing = [];
  if(!ptnHasOrganization($card)) {
    missing.push(ptnText.draftFieldOrg);
  }
  if(projectEditLeader && partnerRespRequired && !$.trim($card.find('textarea.resp').val())) {
    missing.push(ptnText.draftFieldResp);
  }
  if(partnerOfficeRequired && !ptnCountries($card).length) {
    missing.push(ptnText.draftFieldCountry);
  }
  // A managing partner is not linked through anyone: only the others need this
  if(ptnHasOrganization($card) && !ptnIsPPA($card) && !ptnLinkedNames($card).length) {
    missing.push(ptnText.draftFieldLinked);
  }
  return missing;
}

function ptnRefreshDraftNote($card) {
  var tried = $card.hasClass('is-tried');
  var $note = $card.find('[data-ptn-draft-note]');
  if(!tried) {
    var required = [
      ptnText.draftFieldOrg
    ];
    if(projectEditLeader && partnerRespRequired) {
      required.push(ptnText.draftFieldResp);
    }
    if(partnerOfficeRequired) {
      required.push(ptnText.draftFieldCountry);
    }
    if(!ptnHasOrganization($card) || !ptnIsPPA($card)) {
      required.push(ptnText.draftFieldLinked);
    }
    $note.text(ptnText_('draftRequired', [
      required.join(', ')
    ])).removeClass('is-error');
  } else {
    var missing = ptnDraftMissing($card);
    $note.text(missing.length ? ptnText_('draftMissing', [
      missing.join(', ')
    ]) : ptnText.draftReady).toggleClass('is-error', missing.length > 0);
  }
  $card.find('[data-ptn-org-error]').prop('hidden', !(tried && !ptnHasOrganization($card)));
  $card.find('.partnerName').toggleClass('is-invalid', tried && !ptnHasOrganization($card));

  // Where the first country office came from
  var $auto = $card.find('.locElement [data-ptn-auto]:not([hidden])').first();
  $card.find('[data-ptn-country-note]').text(
      $auto.exists() ? ptnText_('countryFilled', [
        $.trim($auto.closest('.locElement').find('.name').text())
      ]) + ' ' : '');
}

function ptnCancelDraft($card) {
  $card.remove();
  $('.addProjectPartner').prop('disabled', false);
  setProjectPartnersIndexes();
  updateProjectPPAPartnersLists();
  ptnRefreshAll();
}

function ptnCommitDraft($card) {
  $card.addClass('is-tried');
  ptnRefreshCard($card);
  if(ptnDraftMissing($card).length) {
    return;
  }
  $card.removeClass('is-draft is-tried');
  $card.find('[data-ptn-country-note]').text('');
  $card.find('[data-ptn-draft-commit], [data-ptn-draft-cancel]').prop('disabled', true);
  ptnRefreshAll();
  ptnBump();
  // "Add to cluster" saves the section straight away, through the regular Save button so
  // its checks (organization, justification) and the server's validation still apply.
  var $save = $('.ptn-saveBar button[name="save"], .ptn-saveBar .button-save').first();
  if($save.exists()) {
    $save[0].click();
  }
}

/* ------------------------------------------------------------------------------------------------
 * Contact people
 * --------------------------------------------------------------------------------------------- */

/** "Add coordinator" and the like: a new row under that role, filled from the users popup. */
function addContactEvent(e) {
  e.preventDefault();
  var role = $(this).data('ptnAddContact');
  var $card = $(this).closest('.projectPartner');
  var partner = new PartnerObject($card);
  var $newElement = $("#contactPerson-template").clone(true).removeAttr("id");
  $newElement.find('.partnerPersonType').val(role);
  $newElement.removeClass(partnerPersonTypes.join(' ')).addClass(role + ' is-pending');
  $card.find('[data-ptn-group="' + role + '"] .ptn-group__members').append($newElement);
  $newElement.css('display', 'flex');

  // IFPRI Division
  if(partner.institutionId == 89) {
    $newElement.find('.divisionBlock.division-IFPRI').show();
  }
  $newElement.find('select.setSelect2').select2({
      width: '100%'
  });

  // Update indexes
  setProjectPartnersIndexes();

  ptnPendingContact = {
      $person: $newElement,
      role: role
  };
  openSearchDialog($newElement.find('input.userName'));
}

/** Splits the popup's "Last, First <email>" when it does not hand over the parts. */
function ptnParseComposedName(composedName) {
  var match = /^(.*?),\s*(.*?)\s*<(.*)>$/.exec(composedName || '');
  return match ? {
      lName: match[1],
      fName: match[2],
      email: match[3]
  } : {
      lName: composedName || '',
      fName: '',
      email: ''
  };
}

function ptnPickUser($person,composedName,userId,user) {
  var $card = $person.closest('.projectPartner');
  var pending = ptnPendingContact && $person.is(ptnPendingContact.$person) ? ptnPendingContact : null;
  var $existing = $card.find('.contactPerson').not($person).filter(function() {
    return $(this).find('input.userId').val() == userId;
  });

  if($existing.exists()) {
    if(pending && pending.role == leaderType) {
      // "Replace leader" with someone already listed here: their row becomes the leader
      ptnPendingContact = null;
      $person.remove();
      ptnMakeLeader($existing.first());
    } else {
      var notyOptions = jQuery.extend({}, notyDefaultOptions);
      notyOptions.text = ptnText.duplicateContact || 'Contact person cannot be repeated';
      noty(notyOptions);
    }
    return;
  }

  var parsed = ptnParseComposedName(composedName);
  var first = user.fName || parsed.fName;
  var last = user.lName || parsed.lName;
  var email = user.email || parsed.email;
  $person.find('input.userName').val(composedName);
  $person.find('input.userId').val(userId);
  $person.find('[data-ptn-person-name]').text($.trim(first + ' ' + last));
  $person.find('[data-ptn-person-email]').text(email).attr('href', 'mailto:' + email);
  $person.find('[data-ptn-initials]').text(ptnInitials(first, last));
  $person.removeClass('is-pending');

  if(pending) {
    ptnPendingContact = null;
    if(pending.role == leaderType) {
      ptnMakeLeader($person);
    } else {
      ptnAfterContactChange($card);
    }
  } else {
    ptnAfterContactChange($card);
  }
}

/** Makes this contact the project leader; whoever led before becomes a collaborator. */
function ptnMakeLeader($person) {
  var previous = getProjectLeader();
  var previousName = '';
  if(!jQuery.isEmptyObject(previous)) {
    previousName = $.trim($partnersBlock.find('.contactPerson').filter(function() {
      return new PartnerPersonObject($(this)).isLeader() && !$(this).is($person);
    }).first().find('[data-ptn-person-name]').text());
  }
  setPartnerTypeToDefault(leaderType, $person);
  var contact = new PartnerPersonObject($person);
  contact.type = leaderType;
  contact.changeType();
  ptnPlaceInGroup($person, leaderType);
  setProjectLeader(getProjectLeader());
  ptnAfterContactChange($person.closest('.projectPartner'));
  if(previousName) {
    ptnShowToast(ptnText_('replaceConfirm', [
      previousName
    ]));
  }
}

function ptnAfterContactChange($card) {
  new PartnerObject($card).changeType();
  setProjectPartnersIndexes();
  updateProjectPPAPartnersLists();
  ptnCards().each(function() {
    ptnRefreshCard($(this));
  });
  ptnRefreshSummary();
  ptnApplyFilters();
  ptnBumpIn($card);
}

function removePersonEvent(e) {
  e.preventDefault();
  var $person = $(this).closest('.contactPerson');
  var person = new PartnerPersonObject($person);
  var notyOptions = jQuery.extend({}, notyDefaultOptions);
  // Validate if the person type is PL
  if(person.isLeader()) {
    notyOptions.text = ptnText.personRemoveBlockedLeader;
    noty(notyOptions);
    return;
  }
  // Validate if there are any activity linked to this person
  var activities = person.getRelationsNumber('activities');
  if(activities > 0) {
    notyOptions.text = ptnText_('personRemoveBlockedActivities', [
      activities
    ]);
    noty(notyOptions);
    return;
  }
  // Validate if there are any deliverable linked to this person
  var deliverables = person.getRelationsNumber('deliverables');
  if(deliverables > 0) {
    notyOptions.text = ptnText_('personRemoveConfirm', [
      deliverables
    ]);
    notyOptions.type = 'confirm';
    notyOptions.layout = 'center';
    notyOptions.modal = true;
    notyOptions.buttons = [
        {
            addClass: 'btn btn-danger',
            text: 'Remove',
            onClick: function($noty) {
              $noty.close();
              ptnRemovePerson($person);
            }
        }, {
            addClass: 'btn btn-default',
            text: 'Cancel',
            onClick: function($noty) {
              $noty.close();
            }
        }
    ];
    noty(notyOptions);
    return;
  }
  ptnRemovePerson($person);
}

/** Takes a contact out of the form, with an undo while the toast is up. */
function ptnRemovePerson($person) {
  var $card = $person.closest('.projectPartner');
  var $parent = $person.parent();
  var $prev = $person.prev();
  var name = $.trim($person.find('[data-ptn-person-name]').text());
  $person.detach();
  new PartnerObject($card).changeType();
  setProjectPartnersIndexes();
  ptnRefreshCard($card);
  ptnRefreshSummary();
  ptnBumpIn($card);
  ptnShowToast(ptnText_('removed', [
    name
  ]), function() {
    if($prev.exists() && $prev.parent().exists()) {
      $prev.after($person);
    } else {
      $parent.prepend($person);
    }
    new PartnerObject($card).changeType();
    setProjectPartnersIndexes();
    ptnRefreshCard($card);
    ptnRefreshSummary();
    ptnBumpIn($card);
  });
}

function setProjectPartnersIndexes() {
  $partnersBlock.find(".projectPartner").each(function(index,element) {
    var partner = new PartnerObject($(element));
    partner.setIndex(index);
  });
}

// Activate the select2 plugin on the organization and division lists. The country office
// picker stays a native select: the design draws it as an inline "+ Add country" control.
function addSelect2() {

  // Organization / institution
  $("form select.institutionsList").select2(searchInstitutionsOptions(canUpdatePPAPartners));
  $("form select.institutionsList").parent().find("span.select2-selection__placeholder").text(placeholderText);

  $('select.countriesRequest').select2({
      placeholder: "Select a country(ies)",
      templateResult: formatStateCountries,
      templateSelection: formatStateCountries,
      width: '100%'
  });

  // Other selects
  $("form select.setSelect2 ").select2({
      width: '100%'
  });

}

/**
 * PartnerObject
 *
 * @param {DOM} Project partner
 */

function PartnerObject(partner) {

  var types = [];
  this.id = parseInt($(partner).find('.partnerId').val());
  this.institutionId = parseInt($(partner).find('.institutionsList').val());
  if(isNaN(this.institutionId)) {
    this.institutionId = -1;
  }
  this.institutionName =
      $('#instID-' + this.institutionId + ' .composedName').text() || $(partner).find('.partnerTitle').text();
  this.allowSubDepart = ($('#instID-' + this.institutionId + ' .allowSubDepart').text() === "true") || false;
  this.ppaPartnersList = $(partner).find('.ppaPartnersList');
  this.persons = $(partner).find('.contactsPerson .contactPerson');
  this.countriesSelect = $(partner).find('.countriesList');
  this.setIndex = function(index) {

    // Updating indexes
    $(partner).setNameIndexes(1, index);
    // Update index for the linked managing partners
    $(partner).find('.ppaPartnersList ul.list li').each(function(li_index,li) {
      $(li).setNameIndexes(2, li_index);
    });

    // Update index for partner persons
    $(partner).find('.contactPerson').each(function(person_index,partnerPerson) {
      var contact = new PartnerPersonObject($(partnerPerson));
      contact.setIndex(index, person_index);
    });

    // Update index for locations
    $(partner).find('.locElement').each(function(i,element) {
      $(element).setNameIndexes(2, i);
    });
  };
  this.validateGovernmentType = function() {
    if(this.allowSubDepart) {
      $(partner).find('.subDepartment').slideDown();
    } else {
      $(partner).find('.subDepartment').slideUp();
    }
  };
  this.updateBlockContent = function() {
    $(partner).find('.partnerTitle').text(this.institutionName);
    this.validateGovernmentType();
  };
  this.hasPartnerContributions = function() {
    var partners = [];
    var institutionId = this.institutionId;
    $partnersBlock.find(".projectPartner").each(function(index,element) {
      var projectPartner = new PartnerObject($(element));
      $(element).find('.ppaPartnersList ul.list li input.id').each(function(i_id,id) {
        if($(id).val() == institutionId) {
          partners.push($.trim($(element).find('.ptn-card__acr').text()) || projectPartner.institutionName);
        }
      });
    });
    return partners;
  };
  this.hasLeader = function() {
    var result = false;
    $(partner).find('.contactPerson').each(function(i,partnerPerson) {
      var contact = new PartnerPersonObject($(partnerPerson));
      if(contact.isLeader()) {
        result = true;
      }
    });
    return result;
  };
  this.isPPA = function() {
    var instID = parseInt($(partner).find('.institutionsList').val());
    return allPPAInstitutions.indexOf(instID) != -1;
  };
  this.getRelationsNumber = function(relation) {
    var count = 0;
    $(partner).find('.contactPerson').each(function(i,partnerPerson) {
      var contact = new PartnerPersonObject($(partnerPerson));
      count += contact.getRelationsNumber(relation);
    });
    return count;
  };
  this.checkLeader = function() {
    if($(partner).find('.contactPerson.PL').length == 0) {
      $(partner).removeClass('leader');
    } else {
      $(partner).addClass('leader');
      types.push('Leader');
    }
  };
  this.checkCoordinator = function() {
    if($(partner).find('.contactPerson.PC').length == 0) {
      $(partner).removeClass('coordinator');
    } else {
      $(partner).addClass('coordinator');
      types.push('Coordinator');
    }
  };
  this.changeType = function() {
    types = [];
    this.checkLeader();
    this.checkCoordinator();
  };
  this.clearCountries = function() {
    var $list = $(partner).find(".countries-list.items-list ul");
    $list.empty();
  };
  this.addCountry = function(country) {
    var contryISO = country.iso;
    var countryName = country.name;
    if(!contryISO || contryISO == "-1") {
      return

    }

    var $list = $(partner).find(".items-list ul");

    var selectedCountries = $list.find('.locElement').map(function() {
      return $(this).find('input.locElementCountry').val();
    }).get();

    if(selectedCountries.indexOf(contryISO) != -1) {
      var notyOptions = jQuery.extend({}, notyDefaultOptions);
      notyOptions.text = 'Countries office cannot be repeated';
      noty(notyOptions);
      return

    }

    var $item = $('#locElement-template').clone(true).removeAttr('id');

    // Fill item values
    $item.find('span.name').text(countryName);
    $item.find('input.locElementCountry').val(contryISO);
    $item.find('.removeLocElement').attr('aria-label', ptnText_('countryRemove', [
      countryName
    ]) || countryName);
    // "From location": the office the organization's headquarters filled in
    $item.find('[data-ptn-auto]').prop('hidden', !country.auto);

    // Add Flag
    $item.find('.flag-icon').html('<i class="flag-icon flag-icon-' + contryISO.toLowerCase() + '"></i>');
    // Adding item to the list
    $list.append($item);
    $item.css('display', 'inline-flex');
    // Update Locations Indexes
    setProjectPartnersIndexes();

    // Reset select
    $(this.countriesSelect).removeOption(contryISO);
    $(this.countriesSelect).val('-1');
  };
  this.showPPAs = function() {
    $(this.ppaPartnersList).show();
    $(partner).find('.contactsPerson .requiredTag').hide();
  };
  this.hidePPAs = function() {
    $(this.ppaPartnersList).hide();
    $(partner).find('.contactsPerson .requiredTag').show();
  };

  this.startLoader = function() {
    $(partner).find('.loading').fadeIn();
  };
  this.stopLoader = function() {
    $(partner).find('.loading').fadeOut();
  };
}

/**
 * PartnerPersonObject
 *
 * @param {DOM} Partner person
 */
function PartnerPersonObject(partnerPerson) {
  this.id = parseInt($(partnerPerson).find('.partnerPersonId').val());
  this.type = $(partnerPerson).find('.partnerPersonType').val();
  this.contactInfo = $.trim($(partnerPerson).find('[data-ptn-person-name]').text())
      || $(partnerPerson).find('.userName').val();
  this.canEditEmail = ($(partnerPerson).find('input.canEditEmail').val() === "true");
  this.setPartnerType = function(type) {
    this.type = type;
    $(partnerPerson).find('.partnerPersonType').val(type);
  };
  this.getPartnerType = function() {
    return $(partnerPerson).find('.partnerPersonType').val();
  };
  this.changeType = function() {
    $(partnerPerson).removeClass(partnerPersonTypes.join(' ')).addClass(this.type);
    this.setPartnerType(this.type);
  };
  this.getRelationsNumber = function(relation) {
    return parseInt($(partnerPerson).find('.tag.' + relation + ' span').text()) || 0;
  };
  this.getRelations = function(relation) {
    return $(partnerPerson).find('.tag.' + relation).next().find('ul').html();
  };
  this.setIndex = function(partnerIndex,index) {
    // Update Indexes
    $(partnerPerson).setNameIndexes(2, index);

    // Update name & id for unused input
    $(partnerPerson).find(".userName").attr("name", "partner-" + partnerIndex + "-person-" + index);
    $(partnerPerson).find(".userName").attr("id", "partner-" + partnerIndex + "-person-" + index);

  };
  this.isLeader = function() {
    return(this.type == leaderType);
  };
}

function formatStateCountries(state) {
  if(!state.id) {
    return state.text;
  }
  var flag = '<i class="flag-icon flag-icon-' + state.element.value.toLowerCase() + '"></i> ';
  var $state;
  if(state.id != -1) {
    $state = $('<span>' + flag + state.text + '</span>');
  } else {
    $state = $('<span>' + state.text + '</span>');
  }
  return $state;
};

// Locations (Country Offices)
function addLocElementCountry() {
  var $partner = $(this).parents('.projectPartner');
  var partner = new PartnerObject($partner);

  var $countrySelected = $(this).find("option:selected");
  partner.addCountry({
      iso: $countrySelected.val(),
      name: $countrySelected.text()
  });
  ptnRefreshCard($partner);
  ptnBumpIn($partner);
}

function removeLocElement(e) {
  e.preventDefault();
  var $parent = $(this).closest('.locElement');
  var $partner = $parent.closest('.projectPartner');
  var $select = $parent.closest('.countries-list').find('select.countriesList');
  // Add removed item to the selection list
  $select.addOption($parent.find('input.locElementCountry').val(), $.trim($parent.find('span.name').text()));
  // Removing item
  $parent.remove();
  setProjectPartnersIndexes();
  ptnRefreshCard($partner);
  ptnBumpIn($partner);
}
