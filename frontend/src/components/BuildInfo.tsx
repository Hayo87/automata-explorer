import { Stats } from "../types/BuildResponse";

interface BuildInfoProps {
  stats: Stats
  reference: string;
  subject: string;
}

const BuildInfo: React.FC<BuildInfoProps> = ({ reference, subject, stats }) => {
    
    return (
        <div>
            <h2><center>Build Result</center></h2>
            <hr></hr>
            <div className = 'buildInfo'>
                <div className = 'buildInfo-column'>
                    <strong>Inputs</strong>
                    <div className = 'reference-file-name'><p>Reference: {reference} </p></div>
                    <div className = 'subject-file-name'><p>Subject: {subject} </p></div>

                    <strong>Matching</strong>
                    <p>Matched edges: {stats.unchangedEdges} </p>
                    <p>Unmatched edges: {stats.totalEdges - stats.unchangedEdges}</p>

                    <strong>Metadata</strong>
                    <p>Number of Nodes: {stats.totalNodes} </p>
                    <p>Number of Edges: {stats.totalEdges}</p>
                </div>
                <div className = "buildInfo-column">
                    <p className="percentage">{((stats.unchangedEdges / stats.totalEdges) * 100).toFixed(0)} %</p>
                </div>    
            </div>
        </div>
    )
}

export default BuildInfo;