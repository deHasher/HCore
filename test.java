	private final String numberPropertyName = "belowname-number";
	private final String textPropertyName   = "belowname-text";

	private TAB tab;
	private String rawNumber;
	private String rawText;
	private List<String> usedPlaceholders;
	private List<String> disabledWorlds;

	// Главный метод.
	public BelowName(TAB tab) {
		this.tab       = tab;

		rawNumber      = tab.getConfiguration().config.getString("classic-vanilla-belowname.number", "%health%");
		rawText        = tab.getConfiguration().config.getString("classic-vanilla-belowname.text", "Health");

		disabledWorlds = tab.getConfiguration().config.getStringList("disable-features-in-"+tab.getPlatform().getSeparatorType()+"s.belowname", Arrays.asList("disabled" + tab.getPlatform().getSeparatorType()));

		refreshUsedPlaceholders();

		tab.debug(String.format("Loaded BelowName feature with parameters number=%s, text=%s, disabledWorlds=%s", rawNumber, rawText, disabledWorlds));

		tab.getFeatureManager().registerFeature("belowname-text", new Refreshable() {
			private List<String> usedPlaceholders;
			{refreshUsedPlaceholders();}

			@Override
			public void refresh(TabPlayer refreshed, boolean force) {
				if (isDisabledWorld(disabledWorlds, refreshed.getWorldName())) return;
				refreshed.sendCustomPacket(new PacketPlayOutScoreboardObjective(2, ObjectiveName, refreshed.getProperty(textPropertyName).updateAndGet(), EnumScoreboardHealthDisplay.INTEGER), TabFeature.BELOWNAME_TEXT);
			}

			@Override
			public List<String> getUsedPlaceholders() {
				return usedPlaceholders;
			}

			@Override
			public void refreshUsedPlaceholders() {
				usedPlaceholders = tab.getPlaceholderManager().getUsedPlaceholderIdentifiersRecursive(rawText);
			}

			@Override
			public TabFeature getFeatureType() {
				return TabFeature.BELOWNAME_TEXT;
			}
		});
	}




	@Override
	public void load() {
		for (TabPlayer loaded : tab.getPlayers()) {
			loaded.setProperty(numberPropertyName, rawNumber);
			loaded.setProperty(textPropertyName, rawText);
			if (isDisabledWorld(disabledWorlds, loaded.getWorldName())) continue;
			PacketAPI.registerScoreboardObjective(loaded, ObjectiveName, loaded.getProperty(textPropertyName).updateAndGet(), DisplaySlot, EnumScoreboardHealthDisplay.INTEGER, TabFeature.BELOWNAME_TEXT);
		}
		for (TabPlayer viewer : tab.getPlayers()) {
			for (TabPlayer target : tab.getPlayers()) {
				viewer.sendCustomPacket(new PacketPlayOutScoreboardScore(Action.CHANGE, ObjectiveName, target.getName(), getValue(target)), TabFeature.BELOWNAME_NUMBER);
			}
		}
	}

	@Override
	public void onJoin(TabPlayer connectedPlayer) {
		connectedPlayer.setProperty(numberPropertyName, rawNumber);
		connectedPlayer.setProperty(textPropertyName, rawText);
		if (isDisabledWorld(disabledWorlds, connectedPlayer.getWorldName())) return;
		PacketAPI.registerScoreboardObjective(connectedPlayer, ObjectiveName, connectedPlayer.getProperty(textPropertyName).get(), DisplaySlot, EnumScoreboardHealthDisplay.INTEGER, TabFeature.BELOWNAME_TEXT);
		int number = getValue(connectedPlayer);
		for (TabPlayer all : tab.getPlayers()) {
			all.sendCustomPacket(new PacketPlayOutScoreboardScore(Action.CHANGE, ObjectiveName, connectedPlayer.getName(), number), TabFeature.BELOWNAME_NUMBER);
			if (all.isLoaded()) connectedPlayer.sendCustomPacket(new PacketPlayOutScoreboardScore(Action.CHANGE, ObjectiveName, all.getName(), getValue(all)), TabFeature.BELOWNAME_NUMBER);
		}
	}

	// Выдаёт 0 если значение belowname-number у игрока является ХУЕВЫМ!
	private int getValue(TabPlayer player) {
		return tab.getErrorManager().parseInteger(player.getProperty(numberPropertyName).updateAndGet(), 0, "belowname");
	}

	// тут обновляется и изменяется число
	@Override
	public void refresh(TabPlayer player, boolean force) {
		if (isDisabledWorld(disabledWorlds, player.getWorldName())) return;
		int number = getValue(player);
		for (TabPlayer all : tab.getPlayers()) {
			all.sendCustomPacket(new PacketPlayOutScoreboardScore(Action.CHANGE, ObjectiveName, player.getName(), number), TabFeature.BELOWNAME_NUMBER);
		}
	}

	// Зачем-то обновляет значения number...
	@Override
	public void refreshUsedPlaceholders() {
		usedPlaceholders = tab.getPlaceholderManager().getUsedPlaceholderIdentifiersRecursive(rawNumber);
	}

	@Override
	public void onLoginPacket(TabPlayer packetReceiver) {
		if (isDisabledWorld(disabledWorlds, packetReceiver.getWorldName())) return;
		PacketAPI.registerScoreboardObjective(packetReceiver, ObjectiveName, packetReceiver.getProperty(textPropertyName).get(), DisplaySlot, EnumScoreboardHealthDisplay.INTEGER, TabFeature.BELOWNAME_TEXT);
		for (TabPlayer all : tab.getPlayers()){
			if (all.isLoaded()) packetReceiver.sendCustomPacket(new PacketPlayOutScoreboardScore(Action.CHANGE, ObjectiveName, all.getName(), getValue(all)), TabFeature.BELOWNAME_NUMBER);
		}
	}
