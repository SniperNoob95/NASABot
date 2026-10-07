package org.nasabot.nasabot.objects.neo;

import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class OrbitalData {
    private final String orbitId;
    private final String orbitDeterminationDate;
    private final String firstObservationDate;
    private final String lastObservationDate;
    private final String dataArcInDays;
    private final String observationsUsed;
    private final String orbitUncertainty;
    private final String minimumOrbitIntersection;
    private final String jupiterTisserandInvariant;
    private final String epochOsculation;
    private final String eccentricity;
    private final String semiMajorAxis;
    private final String inclination;
    private final String ascendingNodeLongitude;
    private final String orbitalPeriod;
    private final String perihelionDistance;
    private final String perihelionArgument;
    private final String aphelionDistance;
    private final String perihelionTime;
    private final String meanAnomaly;
    private final String meanMotion;
    private final String equinox;
    private final String orbitClassType;
    private final String orbitClassDescription;
    private final String orbitClassRange;

    public OrbitalData(String orbitId, String orbitDeterminationDate, String firstObservationDate,
                       String lastObservationDate, String dataArcInDays, String observationsUsed,
                       String orbitUncertainty, String minimumOrbitIntersection,
                       String jupiterTisserandInvariant, String epochOsculation,
                       String eccentricity, String semiMajorAxis, String inclination,
                       String ascendingNodeLongitude, String orbitalPeriod,
                       String perihelionDistance, String perihelionArgument,
                       String aphelionDistance, String perihelionTime,
                       String meanAnomaly, String meanMotion, String equinox,
                       String orbitClassType, String orbitClassDescription,
                       String orbitClassRange) {
        this.orbitId = orbitId;
        this.orbitDeterminationDate = orbitDeterminationDate;
        this.firstObservationDate = firstObservationDate;
        this.lastObservationDate = lastObservationDate;
        this.dataArcInDays = dataArcInDays;
        this.observationsUsed = observationsUsed;
        this.orbitUncertainty = orbitUncertainty;
        this.minimumOrbitIntersection = minimumOrbitIntersection;
        this.jupiterTisserandInvariant = jupiterTisserandInvariant;
        this.epochOsculation = epochOsculation;
        this.eccentricity = eccentricity;
        this.semiMajorAxis = semiMajorAxis;
        this.inclination = inclination;
        this.ascendingNodeLongitude = ascendingNodeLongitude;
        this.orbitalPeriod = orbitalPeriod;
        this.perihelionDistance = perihelionDistance;
        this.perihelionArgument = perihelionArgument;
        this.aphelionDistance = aphelionDistance;
        this.perihelionTime = perihelionTime;
        this.meanAnomaly = meanAnomaly;
        this.meanMotion = meanMotion;
        this.equinox = equinox;
        this.orbitClassType = orbitClassType;
        this.orbitClassDescription = orbitClassDescription;
        this.orbitClassRange = orbitClassRange;
    }

    public String getOrbitId() {
        return orbitId;
    }

    public String getOrbitDeterminationDate() {
        return orbitDeterminationDate;
    }

    public String getFirstObservationDate() {
        return firstObservationDate;
    }

    public String getLastObservationDate() {
        return lastObservationDate;
    }

    public String getDataArcInDays() {
        return dataArcInDays;
    }

    public String getObservationsUsed() {
        return observationsUsed;
    }

    public String getOrbitUncertainty() {
        return orbitUncertainty;
    }

    public String getMinimumOrbitIntersection() {
        return minimumOrbitIntersection;
    }

    public String getJupiterTisserandInvariant() {
        return jupiterTisserandInvariant;
    }

    public String getEpochOsculation() {
        return epochOsculation;
    }

    public String getEccentricity() {
        return eccentricity;
    }

    public String getSemiMajorAxis() {
        return semiMajorAxis;
    }

    public String getInclination() {
        return inclination;
    }

    public String getAscendingNodeLongitude() {
        return ascendingNodeLongitude;
    }

    public String getOrbitalPeriod() {
        return orbitalPeriod;
    }

    public String getPerihelionDistance() {
        return perihelionDistance;
    }

    public String getPerihelionArgument() {
        return perihelionArgument;
    }

    public String getAphelionDistance() {
        return aphelionDistance;
    }

    public String getPerihelionTime() {
        return perihelionTime;
    }

    public String getMeanAnomaly() {
        return meanAnomaly;
    }

    public String getMeanMotion() {
        return meanMotion;
    }

    public String getEquinox() {
        return equinox;
    }

    public String getOrbitClassType() {
        return orbitClassType;
    }

    public String getOrbitClassDescription() {
        return orbitClassDescription;
    }

    public String getOrbitClassRange() {
        return orbitClassRange;
    }

    public Container renderContainer(int returnPage, String neoId, String neoName) {
        String comet = Emoji.fromUnicode("U+2604").getFormatted();

        List<ContainerChildComponent> children = new ArrayList<>();
        children.add(TextDisplay.of(String.format("# %s %s — Orbital Data", comet, neoName != null ? neoName : "NEO")));
        children.add(Separator.create(true, Separator.Spacing.LARGE));

        if (orbitClassType != null && !orbitClassType.isEmpty() && !orbitClassType.equals("N/A")) {
            children.add(TextDisplay.of("### Orbit Class"));
            StringBuilder classBuilder = new StringBuilder(String.format("**%s**", orbitClassType));
            if (orbitClassDescription != null && !orbitClassDescription.isEmpty() && !orbitClassDescription.equals("N/A")) {
                classBuilder.append(String.format(" — %s", orbitClassDescription));
            }
            if (orbitClassRange != null && !orbitClassRange.isEmpty() && !orbitClassRange.equals("N/A")) {
                classBuilder.append(String.format("\n*Range:* %s", orbitClassRange));
            }
            children.add(TextDisplay.of(classBuilder.toString()));
            children.add(Separator.create(true, Separator.Spacing.LARGE));
        }

        children.add(TextDisplay.of("### Orbital Elements"));
        children.add(TextDisplay.of(String.format(
                "• **Semi-Major Axis:** %s AU\n" +
                "• **Eccentricity:** %s\n" +
                "• **Inclination:** %s°\n" +
                "• **Orbital Period:** %s days\n" +
                "• **Perihelion Distance:** %s AU\n" +
                "• **Aphelion Distance:** %s AU",
                semiMajorAxis != null ? semiMajorAxis : "N/A",
                eccentricity != null ? eccentricity : "N/A",
                inclination != null ? inclination : "N/A",
                orbitalPeriod != null ? orbitalPeriod : "N/A",
                perihelionDistance != null ? perihelionDistance : "N/A",
                aphelionDistance != null ? aphelionDistance : "N/A"
        )));
        children.add(Separator.create(true, Separator.Spacing.LARGE));

        children.add(TextDisplay.of("### Trajectory & Angles"));
        children.add(TextDisplay.of(String.format(
                "• **Ascending Node Longitude:** %s°\n" +
                "• **Perihelion Argument:** %s°\n" +
                "• **Mean Anomaly:** %s°\n" +
                "• **Mean Motion:** %s°/day\n" +
                "• **Perihelion Time:** %s TDB",
                ascendingNodeLongitude != null ? ascendingNodeLongitude : "N/A",
                perihelionArgument != null ? perihelionArgument : "N/A",
                meanAnomaly != null ? meanAnomaly : "N/A",
                meanMotion != null ? meanMotion : "N/A",
                perihelionTime != null ? perihelionTime : "N/A"
        )));
        children.add(Separator.create(true, Separator.Spacing.LARGE));

        children.add(TextDisplay.of("### Orbital Intersection & Physics"));
        children.add(TextDisplay.of(String.format(
                "• **Minimum Orbit Intersection (MOID):** %s AU\n" +
                "• **Jupiter Tisserand Invariant:** %s\n" +
                "• **Orbit Uncertainty:** %s",
                minimumOrbitIntersection != null ? minimumOrbitIntersection : "N/A",
                jupiterTisserandInvariant != null ? jupiterTisserandInvariant : "N/A",
                orbitUncertainty != null ? orbitUncertainty : "N/A"
        )));
        children.add(Separator.create(true, Separator.Spacing.LARGE));

        children.add(TextDisplay.of("### Observation History"));
        children.add(TextDisplay.of(String.format(
                "• **Orbit ID:** %s\n" +
                "• **Determination Date:** %s\n" +
                "• **Epoch Osculation:** %s\n" +
                "• **Equinox:** %s\n" +
                "• **Observations Used:** %s (%s days data arc, %s to %s)",
                orbitId != null ? orbitId : "N/A",
                orbitDeterminationDate != null ? orbitDeterminationDate : "N/A",
                epochOsculation != null ? epochOsculation : "N/A",
                equinox != null ? equinox : "N/A",
                observationsUsed != null ? observationsUsed : "N/A",
                dataArcInDays != null ? dataArcInDays : "N/A",
                firstObservationDate != null ? firstObservationDate : "N/A",
                lastObservationDate != null ? lastObservationDate : "N/A"
        )));
        children.add(Separator.create(true, Separator.Spacing.LARGE));

        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.success("NEO:VIEW:" + returnPage + ":" + neoId, "Back to NEO"));
        buttons.add(Button.secondary("NEO:HOME:" + returnPage, "Back to List"));
        children.add(ActionRow.of(buttons));

        return Container.of(children).withAccentColor(new Color(128, 0, 128));
    }
}
