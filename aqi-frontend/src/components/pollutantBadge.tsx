interface Props{
    label: string;
    value?: number | null;
}

export default function PollutantBadge({ label, value }: Props){
    return(
        <div className="pollutant-badge">
            <span className="pollutant-label">{label} : </span>
            <span className="pollutant-value">
                {value !== null && value !== undefined ? value.toFixed(1) : "N/A"}
            </span>
        </div>
    );
}