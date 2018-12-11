package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.model.Options.AugmentationFilter;
import l2trunk.gameserver.templates.OptionDataTemplate;

import java.util.*;

public final class OptionDataHolder  {
    private OptionDataHolder() {
    }

    private static final Map<Integer, OptionDataTemplate> templates = new HashMap<>();

    public static void addTemplate(OptionDataTemplate template) {
        templates.put(template.getId(), template);
    }

    public static OptionDataTemplate getTemplate(int id) {
        return templates.get(id);
    }

    public static int size() {
        return templates.size();
    }

    public void clear() {
        templates.clear();
    }

    public static Collection<OptionDataTemplate> getUniqueOptions(AugmentationFilter filter) {
        if (filter == AugmentationFilter.NONE)
            return templates.values();

        final Map<Integer, OptionDataTemplate> options = new HashMap<>();
        switch (filter) {
            case ACTIVE_SKILL: {
                for (OptionDataTemplate option : templates.values()) {
                    // Solo activas
                    if (!option.getTriggerList().isEmpty())
                        continue;

                    if (option.getSkills().isEmpty() || !option.getSkills().get(0).isActive())
                        continue;

                    // Chequeamos que el lvl de esta skill si ya fue agregado, sea mayor al anterior
                    if (!options.containsKey(option.getSkills().get(0).getId()) || options.get(option.getSkills().get(0).getId()).getSkills().get(0).getLevel() < option.getSkills().get(0).getLevel())
                        options.put(option.getSkills().get(0).getId(), option);
                }
                break;
            }
            case PASSIVE_SKILL: {
                for (OptionDataTemplate option : templates.values()) {
                    // Solo pasivas
                    if (!option.getTriggerList().isEmpty())
                        continue;

                    if (option.getSkills().isEmpty() || !option.getSkills().get(0).isPassive())
                        continue;

                    // Chequeamos que el lvl de esta skill si ya fue agregado, sea mayor al anterior
                    if (!options.containsKey(option.getSkills().get(0).getId()) || options.get(option.getSkills().get(0).getId()).getSkills().get(0).getLevel() < option.getSkills().get(0).getLevel())
                        options.put(option.getSkills().get(0).getId(), option);
                }
                break;
            }
            case CHANCE_SKILL: {
                for (OptionDataTemplate option : templates.values()) {
                    // Solo de chance
                    if (option.getTriggerList().isEmpty())
                        continue;

                    if (!options.containsKey(option.getTriggerList().get(0).getSkillId()) || options.get(option.getTriggerList().get(0).getSkillId()).getTriggerList().get(0).getSkillLevel() < option.getTriggerList().get(0).getSkillLevel())
                        options.put(option.getTriggerList().get(0).getSkillId(), option);
                }
                break;
            }
            case STATS: {
                for (OptionDataTemplate option : templates.values()) {
                    switch (option.getId()) {
                        case 16341: // +1 STR
                        case 16342: // +1 CON
                        case 16343: // +1 INT
                        case 16344: // +1 MEN
                            options.put(option.getId(), option);
                            break;
                    }
                }
                break;
            }
        }

        final List<OptionDataTemplate> augs = new ArrayList<>(options.values());
        augs.sort(new AugmentationComparator());

        return augs;
    }

    private static class AugmentationComparator implements Comparator<OptionDataTemplate> {
        @Override
        public int compare(final OptionDataTemplate left, final OptionDataTemplate right) {
            if (left.getSkills().isEmpty() || right.getSkills().isEmpty())
                return 0;

            return Integer.compare(left.getSkills().get(0).getId(), right.getSkills().get(0).getId());
        }
    }

}
