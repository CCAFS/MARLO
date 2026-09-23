-- The Tawk.to chat embeds https://embed.tawk.to/<property>/<widget>, and the widget segment used to be
-- written into the view as the literal 'default'. Tawk names the first widget of a property 'default', so
-- that held for every property created between 2016 and 2020, which is all of the ones in use. A property
-- created now is given a generated widget id instead, and the view has been changed to take the widget
-- from this parameter when the value carries one. This description is how an operator learns that.
--
-- Written in the register of crp_theme_color, the closest neighbour on that screen: what the value is,
-- the shape, and what empty means, in one line. Descriptions there average 49 characters and none reaches
-- 150, and the screen renders this text in small italics inside parentheses, so a paragraph would not read
-- as one. It also has to survive without angle brackets: that view renders the description unescaped, and a
-- browser would read them as tags and drop the text.
--
-- default_value stays NULL on purpose. It is not placeholder text: CrpParametersAction and
-- GlobalUnitCreationManagerImpl both copy it into custom_parameters.value, so an example written there
-- would become the real chat id of every Global Unit created afterwards. An empty value is what correctly
-- means 'this Global Unit has no chat'. The description is therefore the only channel this screen has.
--
-- One row per Global Unit type (1 CRP, 3 Platform, 4 Center), matched by key rather than by id so the
-- statement carries no assumption about which parameter ids a given database has. Both spellings of the key
-- are matched because V2_6_0_20260922_1014 renames 'crp_taw_api' to 'crp_tawk_api' and
-- MarloFlywayConfiguration runs with outOfOrder(true), so the two migrations must not depend on their order.

UPDATE parameters
SET `description` = 'Tawk.to chat for this Global Unit, from its widget code URL: a property id, or propertyId/widgetId. Empty means no chat'
WHERE `key` IN ('crp_taw_api', 'crp_tawk_api');
