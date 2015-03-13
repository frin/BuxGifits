CREATE TABLE IF NOT EXISTS `buxgiftreceipts` (
  `buxgiftreceipts` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `buxgiftid` int(10) unsigned NOT NULL,
  `uuid` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `amount` int(10) unsigned NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`buxgiftreceipts`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `buxgifts` (
  `buxgiftid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `owner_uuid` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `creator_uuid` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `uses` bigint(20) unsigned NOT NULL DEFAULT '0',
  `world` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'world',
  `x` int(10) NOT NULL,
  `y` int(10) NOT NULL,
  `z` int(10) NOT NULL,
  `amount` int(10) unsigned NOT NULL DEFAULT '1',
  `duration` int(10) unsigned NOT NULL DEFAULT '1',
  `duration_type` enum('minute','hour','day') COLLATE utf8_unicode_ci NOT NULL DEFAULT 'day',
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`buxgiftid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;
